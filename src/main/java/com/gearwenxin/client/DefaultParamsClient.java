package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.DefaultParamsBot;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.gearwenxin.common.WenXinUtils.*;
import static com.gearwenxin.common.WenXinUtils.buildUserMessage;

/**
 * @author Ge Mingjia
 * @date 2023/8/4
 */
@Slf4j
public abstract class DefaultParamsClient implements DefaultParamsBot<ChatBaseRequest> {

    /**
     * 获取自定义access-token
     *
     * @return AccessToken
     */
    public abstract String getCustomAccessToken();

    /**
     * 单独设置access-token
     */
    public abstract void setCustomAccessToken(String accessToken);

    /**
     * 获取此模型的历史消息
     *
     * @return 历史消息 Map<String, Queue<Message>>
     */
    public abstract Map<String, Queue<Message>> getMessageHistoryMap();

    /**
     * 初始化此模型的历史消息
     */
    public abstract void initMessageHistoryMap(Map<String, Queue<Message>> map);

    /**
     * 获取模型URL
     *
     * @return URL
     */
    public abstract String getURL();

    /**
     * 获取模型 TAG
     *
     * @return TAG
     */
    public abstract String getTag();

    @Override
    public ChatResponse chatSingle(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);
        BaseRequest request = new BaseRequest();
        request.setMessages(messageQueue);
        log.info(getTag() + "content_singleRequest => {}", request.toString());

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
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setMessages(messageQueue);
        baseRequest.setStream(true);
        log.info(getTag() + "content_singleRequest_stream => {}", baseRequest.toString());
        return ChatUtils.fluxPost(
                getURL(),
                getCustomAccessToken(),
                baseRequest,
                ChatResponse.class);
    }

    @Override
    public ChatResponse chatSingle(ChatBaseRequest chatBaseRequest) {
        if (chatBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chatBaseRequest.validSelf();
        BaseRequest baseRequest = ConvertUtils.convertToBaseRequest(chatBaseRequest);
        log.info(getTag() + "singleRequest => {}", baseRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                getURL(),
                getCustomAccessToken(),
                baseRequest,
                ChatResponse.class);

        return response.block();
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatBaseRequest chatBaseRequest) {
        if (chatBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chatBaseRequest.validSelf();

        BaseRequest baseRequest = ConvertUtils.convertToBaseRequest(chatBaseRequest);
        baseRequest.setStream(true);
        log.info(getTag() + "singleRequest_stream => {}", baseRequest.toString());

        return ChatUtils.fluxPost(
                getURL(),
                getCustomAccessToken(),
                baseRequest,
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

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setMessages(messagesHistory);
        log.info(getTag() + "content_contRequest => {}", baseRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                getURL(),
                getCustomAccessToken(),
                baseRequest,
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

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setMessages(messagesHistory);
        baseRequest.setStream(true);
        log.info("content_contRequest_stream => {}", baseRequest.toString());

        return this.historyFlux(baseRequest, messagesHistory);
    }

    @Override
    public ChatResponse chatCont(ChatBaseRequest chatBaseRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid) || chatBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chatBaseRequest.validSelf();
        BaseRequest baseRequest = ConvertUtils.convertToBaseRequest(chatBaseRequest);
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, key -> new LinkedList<>());

        // 添加到历史
        Message message = buildUserMessage(chatBaseRequest.getContent());
        WenXinUtils.offerMessage(messagesHistory, message);

        baseRequest.setMessages(messagesHistory);
        log.info(getTag() + "contRequest => {}", baseRequest.toString());

        Mono<ChatResponse> response = ChatUtils.monoPost(
                getURL(),
                getCustomAccessToken(),
                baseRequest,
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
    public Flux<ChatResponse> chatContOfStream(ChatBaseRequest chatBaseRequest, String msgUid) {
        if (StringUtils.isBlank(msgUid) || chatBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chatBaseRequest.validSelf();
        BaseRequest baseRequest = ConvertUtils.convertToBaseRequest(chatBaseRequest);
        Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
        Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(msgUid, key -> new LinkedList<>());
        // 添加到历史
        Message message = buildUserMessage(chatBaseRequest.getContent());
        WenXinUtils.offerMessage(messagesHistory, message);

        baseRequest.setMessages(messagesHistory);
        baseRequest.setStream(true);
        log.info(getTag() + "contRequest_stream => {}", baseRequest.toString());

        return this.historyFlux(baseRequest, messagesHistory);
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