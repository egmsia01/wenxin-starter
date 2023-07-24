package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.erniebot.ChatTurbo7BRequest;
import com.gearwenxin.model.erniebot.Turbo7BRequest;
import com.gearwenxin.model.erniebot.ErnieResponse;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.gearwenxin.common.CommonUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public class ErnieBotTurboClient implements CommonBot<ChatTurbo7BRequest> {

    private final String accessToken;

    public static final String PREFIX_MSG_HISTORY_MONO = "Mono_";
    public static final String PREFIX_MSG_HISTORY_FLUX = "Flux_";

    // 每个模型的历史消息Map
    private static final Map<String, Queue<Message>> TURBO_MESSAGES_HISTORY_MAP = new HashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    public ErnieBotTurboClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ErnieResponse chatSingle(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        Turbo7BRequest request = new Turbo7BRequest();
        request.setMessages(messageQueue);
        log.info("content_singleTurboRequest => {}", request.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                request,
                ErnieResponse.class);
        return response.block();
    }

    @Override
    public Flux<ErnieResponse> chatSingleOfStream(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        Turbo7BRequest ernieRequest = new Turbo7BRequest();
        ernieRequest.setMessages(messageQueue);
        ernieRequest.setStream(true);
        log.info("content_singleTurboRequest => {}", ernieRequest.toString());
        return ChatUtils.fluxChat(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ErnieResponse.class);
    }

    @Override
    public ErnieResponse chatSingle(ChatTurbo7BRequest chatTurbo7BRequest) {
        this.validBaseRequest(chatTurbo7BRequest);

        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        log.info("singleRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ErnieResponse.class);

        return response.block();
    }

    @Override
    public Flux<ErnieResponse> chatSingleOfStream(ChatTurbo7BRequest chatTurbo7BRequest) {
        this.validBaseRequest(chatTurbo7BRequest);

        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        ernieRequest.setStream(true);
        log.info("singleRequest => {}", ernieRequest.toString());

        return ChatUtils.fluxChat(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ErnieResponse.class);
    }

    @Override
    public ErnieResponse chatCont(String content, String msgUid) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messagesHistory = TURBO_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        Turbo7BRequest ernieRequest = new Turbo7BRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info("content_multipleTurboRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ErnieResponse.class);
        ErnieResponse ernieResponse = response.block();
        if (ernieResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(ernieResponse.getResult());
        CommonUtils.offerMessage(messagesHistory, messageResult);

        return ernieResponse;
    }

    @Override
    public Flux<ErnieResponse> chatContOfStream(String content, String msgUid) {
        if (StringUtils.isBlank(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messagesHistory = TURBO_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        Turbo7BRequest ernieRequest = new Turbo7BRequest();
        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("content_chatContOfStreamTurboRequest => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
    public ErnieResponse chatCont(ChatTurbo7BRequest chatTurbo7BRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validBaseRequest(chatTurbo7BRequest);
        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        Queue<Message> messagesHistory = TURBO_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatTurbo7BRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        log.info("chatContTurboRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(
                URLConstant.ERNIE_BOT_URL,
                accessToken,
                ernieRequest,
                ErnieResponse.class);
        ErnieResponse ernieResponse = response.block();
        if (ernieResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(ernieResponse.getResult());
        CommonUtils.offerMessage(messagesHistory, messageResult);

        return ernieResponse;
    }

    @Override
    public Flux<ErnieResponse> chatContOfStream(ChatTurbo7BRequest chatTurbo7BRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validBaseRequest(chatTurbo7BRequest);
        Turbo7BRequest ernieRequest = ConvertUtils.chatTurboReq7BToTurboReq(chatTurbo7BRequest);
        Queue<Message> messagesHistory = TURBO_MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatTurbo7BRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("chatContOfStreamTurboRequest => {}", ernieRequest.toString());

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

    public  <T> Flux<ErnieResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            Flux<ErnieResponse> ernieResponse = ChatUtils.fluxChat(
                    URLConstant.ERNIE_BOT_TURBO_URL,
                    accessToken,
                    request,
                    ErnieResponse.class);
            ernieResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
