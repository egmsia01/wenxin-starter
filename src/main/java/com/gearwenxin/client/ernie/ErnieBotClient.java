package com.gearwenxin.client.ernie;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.DefaultBot;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.WenXinUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotClient implements DefaultBot<ChatErnieRequest>, BaseBot {

    protected ErnieBotClient() {
    }

    private String accessToken = null;
    private static final String TAG = "ErnieBotClient_";

    // 每个模型的历史消息Map
    private static Map<String, Queue<Message>> ERNIE_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    private static final String URL = Constant.ERNIE_BOT_URL;

    protected abstract String getAccessToken();

    private String getTag() {
        return TAG;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return ERNIE_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        ERNIE_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public ChatResponse chatSingle(String content) {
        if (content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);
        ErnieRequest request = new ErnieRequest();
        request.setMessages(messageQueue);
        log.info("{}content_singleRequest => {}", getTag(), request.toString());

        Mono<ChatResponse> response = ChatUtils.monoChatPost(
                getURL(),
                getCustomAccessToken(),
                request,
                ChatResponse.class);
        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        if (content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);
        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messageQueue);
        ernieRequest.setStream(true);
        log.info("{}content_singleRequest_stream => {}", getTag(), ernieRequest.toString());
        return ChatUtils.fluxChatPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatSingle(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        log.info("{}singleRequest => {}", getTag(), ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoChatPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);

        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        ernieRequest.setStream(true);
        log.info("{}singleRequest_stream => {}", getTag(), ernieRequest.toString());

        return ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), ernieRequest, ChatResponse.class);
    }

    @Override
    public ChatResponse chatCont(String content, String msgUid) {
        if (content.isBlank() || msgUid.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        WenXinUtils.offerMessage(messagesHistory, message);

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info("{}content_contRequest => {}", getTag(), ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoChatPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);
        ChatResponse chatResponse = response.block();
        if (chatResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(chatResponse.getResult());
        WenXinUtils.offerMessage(messagesHistory, messageResult);

        return chatResponse;
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        if (content.isBlank() || msgUid.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        WenXinUtils.offerMessage(messagesHistory, message);

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("{}content_contRequest_stream => {}", getTag(), ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
    public ChatResponse chatCont(ChatErnieRequest chatErnieRequest, String msgUid) {
        if (msgUid.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validChatErnieRequest(chatErnieRequest);
        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatErnieRequest.getContent());
        WenXinUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        log.info("{}contRequest => {}", getTag(), ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoChatPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);
        ChatResponse chatResponse = response.block();
        if (chatResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(chatResponse.getResult());
        WenXinUtils.offerMessage(messagesHistory, messageResult);

        return chatResponse;
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatErnieRequest chatErnieRequest, String msgUid) {
        if (msgUid.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validChatErnieRequest(chatErnieRequest);
        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatErnieRequest.getContent());
        WenXinUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("{}contRequest_stream => {}", getTag(), ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    public void validChatErnieRequest(ChatErnieRequest request) {

        // 检查content不为空
        if (request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
        // 检查temperature和topP不both有值
        if (request.getTemperature() != null && request.getTopP() != null) {
            log.warn("Temperature and topP cannot both have value");
        }
        // 检查temperature范围
        if (request.getTemperature() != null &&
                (request.getTemperature() <= 0 || request.getTemperature() > 1.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "temperature should be in (0, 1]");
        }
        // 检查topP范围
        if (request.getTopP() != null &&
                (request.getTopP() < 0 || request.getTopP() > 1.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "topP should be in [0, 1]");
        }
        // 检查penaltyScore范围
        if (request.getTemperature() != null &&
                (request.getPenaltyScore() < 1.0 || request.getPenaltyScore() > 2.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "penaltyScore should be in [1, 2]");
        }
    }

    public <T> Flux<ChatResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            Flux<ChatResponse> chatResponse = ChatUtils.fluxChatPost(
                    getURL(),
                    getCustomAccessToken(),
                    request,
                    ChatResponse.class);
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
