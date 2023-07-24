package com.gearwenxin.client.erniebot;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ErnieResponse;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.erniebot.ErnieRequest;
import com.gearwenxin.common.ChatUtils;
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
public class ErnieBotClient implements ErnieBot {

    private final String accessToken;

    public static final String PREFIX_MSG_HISTORY_MONO = "Mono_";
    public static final String PREFIX_MSG_HISTORY_FLUX = "Flux_";

    // 每个模型的历史消息Map
    private static final Map<String, Queue<Message>> MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    public ErnieBotClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ErnieResponse chatSingle(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messageQueue);
        log.info("content_singleErnieRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(URLConstant.ERNIE_BOT_URL, accessToken, ernieRequest);
        return response.block();
    }

    @Override
    public Flux<ErnieResponse> chatSingleOfStream(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildMessageQueue(content);
        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messageQueue);
        ernieRequest.setStream(true);
        log.info("content_singleErnieRequest => {}", ernieRequest.toString());
        return ChatUtils.fluxChat(URLConstant.ERNIE_BOT_URL, accessToken, ernieRequest);
    }

    @Override
    public ErnieResponse chatSingle(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        log.info("singleRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(URLConstant.ERNIE_BOT_URL, accessToken, ernieRequest);

        return response.block();
    }

    @Override
    public Flux<ErnieResponse> chatSingleOfStream(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        ernieRequest.setStream(true);
        log.info("singleRequest => {}", ernieRequest.toString());

        return ChatUtils.fluxChat(URLConstant.ERNIE_BOT_URL, accessToken, ernieRequest);
    }

    @Override
    public ErnieResponse chatCont(String content, String msgUid) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info("content_multipleErnieRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(URLConstant.ERNIE_BOT_URL, accessToken, ernieRequest);
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
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, k -> new LinkedList<>());
        Message message = buildUserMessage(content);
        CommonUtils.offerMessage(messagesHistory, message);

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("content_chatContOfStreamErnieRequest => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
    public ErnieResponse chatCont(ChatErnieRequest chatErnieRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validChatErnieRequest(chatErnieRequest);
        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatErnieRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        log.info("chatContErnieRequest => {}", ernieRequest.toString());

        Mono<ErnieResponse> response = ChatUtils.monoChat(URLConstant.ERNIE_BOT_URL, accessToken, ernieRequest);
        ErnieResponse ernieResponse = response.block();
        if (ernieResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        }
        Message messageResult = buildAssistantMessage(ernieResponse.getResult());
        CommonUtils.offerMessage(messagesHistory, messageResult);

        return ernieResponse;
    }

    @Override
    public Flux<ErnieResponse> chatContOfStream(ChatErnieRequest chatErnieRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        this.validChatErnieRequest(chatErnieRequest);
        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        Queue<Message> messagesHistory = MESSAGES_HISTORY_MAP.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatErnieRequest.getContent());
        CommonUtils.offerMessage(messagesHistory, message);

        ernieRequest.setMessages(messagesHistory);
        ernieRequest.setStream(true);
        log.info("chatContOfStreamErnieRequest => {}", ernieRequest.toString());

        return this.historyFlux(ernieRequest, messagesHistory);
    }

    @Override
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

    private <T> Flux<ErnieResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            ErnieSubscriber subscriber = new ErnieSubscriber(emitter, messagesHistory);
            Flux<ErnieResponse> ernieResponse = ChatUtils.fluxChat(URLConstant.ERNIE_BOT_URL, accessToken, request);
            ernieResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
