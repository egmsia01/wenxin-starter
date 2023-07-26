package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.response.ChatResponse;
import com.gearwenxin.model.chatmodel.ChatTurbo7BRequest;
import com.gearwenxin.model.request.Turbo7BRequest;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.CommonUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotTurboClient implements CommonBot<ChatTurbo7BRequest> {

    private static final String TAG = "ErnieBotTurboClient_";
    public static final String PREFIX_MSG_HISTORY_MONO = "Mono_";
    public static final String PREFIX_MSG_HISTORY_FLUX = "Flux_";

    // 每个模型的历史消息Map
    private static final Map<String, Queue<Message>> MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    protected ErnieBotTurboClient() {
    }

    @Override
    public abstract String getAccessToken();

    @Override
    public ChatResponse chatSingle(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        Turbo7BRequest request = new Turbo7BRequest();
        request.setMessages(messageQueue);
        log.info(TAG + "content_singleRequest => {}", request.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                getAccessToken(),
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
        Turbo7BRequest ernieRequest = new Turbo7BRequest();
        ernieRequest.setMessages(messageQueue);
        ernieRequest.setStream(true);
        log.info(TAG + "content_singleRequest_stream => {}", ernieRequest.toString());
        return ChatUtils.fluxPost(
                URLConstant.ERNIE_BOT_URL,
                getAccessToken(),
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatSingle(ChatTurbo7BRequest chatTurbo7BRequest) {
        this.validBaseRequest(chatTurbo7BRequest);

        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        log.info(TAG + "singleRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                getAccessToken(),
                ernieRequest,
                ChatResponse.class);

        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatTurbo7BRequest chatTurbo7BRequest) {
        this.validBaseRequest(chatTurbo7BRequest);

        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        ernieRequest.setStream(true);
        log.info(TAG + "singleRequest_stream => {}", ernieRequest.toString());

        return ChatUtils.fluxPost(
                URLConstant.ERNIE_BOT_URL,
                getAccessToken(),
                ernieRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatCont(String content, String msgUid) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        Turbo7BRequest ernieRequest = new Turbo7BRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info(TAG + "content_contRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                getAccessToken(),
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
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        Turbo7BRequest ernieRequest = new Turbo7BRequest();
        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("content_contRequest_stream => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
    public ChatResponse chatCont(ChatTurbo7BRequest chatTurbo7BRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validBaseRequest(chatTurbo7BRequest);
        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatTurbo7BRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        log.info(TAG + "contRequest => {}", ernieRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                URLConstant.ERNIE_BOT_URL,
                getAccessToken(),
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
    public Flux<ChatResponse> chatContOfStream(ChatTurbo7BRequest chatTurbo7BRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validBaseRequest(chatTurbo7BRequest);
        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatTurbo7BRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info(TAG + "contRequest_stream => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    public void validBaseRequest(ChatTurbo7BRequest request) {
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
                    URLConstant.ERNIE_BOT_TURBO_URL,
                    getAccessToken(),
                    request,
                    ChatResponse.class);
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
