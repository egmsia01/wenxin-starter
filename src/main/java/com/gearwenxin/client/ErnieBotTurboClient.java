package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.chatmodel.ChatTurboRequest;
import com.gearwenxin.entity.request.TurboRequest;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.DefaultParamsBot;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.WenXinUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotTurboClient implements DefaultParamsBot<ChatTurboRequest>, BaseBot {

    private String accessToken = null;
    private static final String TAG = "ErnieBotTurboClient_";
    private static final String URL = URLConstant.ERNIE_BOT_TURBO_URL;

    // 每个模型的历史消息Map
    private static Map<String, Queue<Message>> TURBO_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    protected ErnieBotTurboClient() {
    }

    protected abstract String getAccessToken();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return TURBO_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        TURBO_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public ChatResponse chatSingle(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);
        TurboRequest request = new TurboRequest();
        request.setMessages(messageQueue);
        log.info(TAG + "content_singleRequest => {}", request.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                getURL(),
                getCustomAccessToken(),
                request,
                ChatResponse.class);
        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);
        TurboRequest ernieRequest = new TurboRequest();
        ernieRequest.setMessages(messageQueue);
        ernieRequest.setStream(true);
        log.info(TAG + "content_singleRequest_stream => {}", ernieRequest.toString());
        return ChatUtils.fluxPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatSingle(ChatTurboRequest chatTurboRequest) {
        this.validBaseRequest(chatTurboRequest);

        TurboRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurboRequest);
        log.info(TAG + "singleRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);

        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatTurboRequest chatTurboRequest) {
        this.validBaseRequest(chatTurboRequest);

        TurboRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurboRequest);
        ernieRequest.setStream(true);
        log.info(TAG + "singleRequest_stream => {}", ernieRequest.toString());

        return ChatUtils.fluxPost(
                getURL(),
                getCustomAccessToken(),
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatCont(String content, String msgUid) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        WenXinUtils.offerMessage(messagesHistory, message);

        TurboRequest ernieRequest = new TurboRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info(TAG + "content_contRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
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
        if (StringUtils.isBlank(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        WenXinUtils.offerMessage(messagesHistory, message);

        TurboRequest ernieRequest = new TurboRequest();
        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("content_contRequest_stream => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
    public ChatResponse chatCont(ChatTurboRequest chatTurboRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validBaseRequest(chatTurboRequest);
        TurboRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurboRequest);
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatTurboRequest.getContent());
        WenXinUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        log.info(TAG + "contRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
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
    public Flux<ChatResponse> chatContOfStream(ChatTurboRequest chatTurboRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validBaseRequest(chatTurboRequest);
        TurboRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurboRequest);
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatTurboRequest.getContent());
        WenXinUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info(TAG + "contRequest_stream => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    public void validBaseRequest(ChatTurboRequest request) {
        // 检查content不为空
        if (StringUtils.isEmpty(request.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
    }

    public <T> Flux<ChatResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            Flux<ChatResponse> chatResponse = ChatUtils.fluxPost(
                    getURL(),
                    getCustomAccessToken(),
                    request,
                    ChatResponse.class);
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
