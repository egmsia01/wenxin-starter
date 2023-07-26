package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.chatmodel.ChatErnieRequest;
import com.gearwenxin.model.response.ChatResponse;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.request.ErnieRequest;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.CommonUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public class ErnieBotClient implements CommonBot<ChatErnieRequest> {

    private final String accessToken;
    private static final String TAG = "ErnieBotClient_";
    public static final String PREFIX_MSG_HISTORY_MONO = "Mono_";
    public static final String PREFIX_MSG_HISTORY_FLUX = "Flux_";

    // 每个模型的历史消息Map
    private static final Map<String, Queue<Message>> ERNIE_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    public ErnieBotClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ChatResponse chatSingle(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        ErnieRequest request = new ErnieRequest();
        request.setMessages(messageQueue);
        log.info(TAG + "content_singleRequest => {}", request.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                request,
                ChatResponse.class);
        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messageQueue);
        ernieRequest.setStream(true);
        log.info(TAG + "content_singleRequest_stream => {}", ernieRequest.toString());
        return ChatUtils.fluxPost(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatSingle(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        log.info(TAG + "singleRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ChatResponse.class);

        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        ernieRequest.setStream(true);
        log.info(TAG + "singleRequest_stream => {}", ernieRequest.toString());

        return ChatUtils.fluxPost(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatCont(String content, String msgUid) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messagesHistory = ERNIE_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info(TAG + "content_contRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ChatResponse.class);
        ChatResponse chatResponse = response.block();
        if (chatResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(chatResponse.getResult());
        CommonUtils.offerMessage(messagesHistory, messageResult);

        return chatResponse;
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        if (StringUtils.isBlank(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messagesHistory = ERNIE_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info(TAG + "content_contRequest_stream => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
    public ChatResponse chatCont(ChatErnieRequest chatErnieRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validChatErnieRequest(chatErnieRequest);
        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        Queue<Message> messagesHistory = ERNIE_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatErnieRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        log.info(TAG + "contRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ChatResponse.class);
        ChatResponse chatResponse = response.block();
        if (chatResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(chatResponse.getResult());
        CommonUtils.offerMessage(messagesHistory, messageResult);

        return chatResponse;
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatErnieRequest chatErnieRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validChatErnieRequest(chatErnieRequest);
        ErnieRequest ernieRequest = ConvertUtils.chatErnieReqToErnieReq(chatErnieRequest);
        Queue<Message> messagesHistory = ERNIE_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatErnieRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info(TAG + "contRequest_stream => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    public void validChatErnieRequest(ChatErnieRequest request) {

        // 检查content不为空
        if (StringUtils.isEmpty(request.getContent())) {
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
            Flux<ChatResponse> chatResponse = ChatUtils.fluxPost(
                    URLConstant.ERNIE_BOT_URL,
                    accessToken,
                    request,
                    ChatResponse.class);
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
