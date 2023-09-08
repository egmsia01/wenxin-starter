package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.DefaultBot;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

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
public abstract class DefaultClient implements DefaultBot<ChatBaseRequest> {

    /**
     * 获取自定义access-token
     */
    public abstract String getCustomAccessToken();

    /**
     * 单独设置access-token
     */
    public abstract void setCustomAccessToken(String accessToken);

    /**
     * 获取此模型的历史消息
     */
    public abstract Map<String, Queue<Message>> getMessageHistoryMap();

    /**
     * 初始化此模型的历史消息
     */
    public abstract void initMessageHistoryMap(Map<String, Queue<Message>> map);

    /**
     * 获取模型URL
     */
    public abstract String getURL();

    /**
     * 获取模型 TAG
     */
    public abstract String getTag();

    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        return Mono.just(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageQueue)
                .map(messageQueue -> BaseRequest.builder().messages(messageQueue).build())
                .doOnNext(request -> log.info(getTag() + "content_singleRequest => {}", request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }


    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        return Mono.just(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageQueue)
                .map(messageQueue -> BaseRequest.builder().messages(messageQueue).stream(true).build())
                .doOnNext(request -> log.info("{}content_singleRequest_stream => {}", getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }

    @Override
    public Mono<ChatResponse> chatSingle(ChatBaseRequest chatBaseRequest) {
        return Mono.just(chatBaseRequest)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .map(ConvertUtils::toBaseRequest)
                .map(BaseRequest.BaseRequestBuilder::build)
                .doOnNext(baseRequest -> log.info("{}singleRequest => {}", getTag(), baseRequest.toString()))
                .flatMap(baseRequest ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class)
                );
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatBaseRequest chatBaseRequest) {
        return Mono.just(chatBaseRequest)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .map(ConvertUtils::toBaseRequest)
                .map(builder -> builder.stream(true).build())
                .doOnNext(baseRequest -> log.info("{}singleRequest_stream => {}", getTag(), baseRequest.toString()))
                .flatMapMany(baseRequest ->
                        ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class)
                );
    }


    @Override
    public Mono<ChatResponse> chatCont(String content, String msgUid) {
        return Mono.just(Tuples.of(content, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()) && StringUtils.isNotBlank(tuple.getT2()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .flatMap(tuple -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messageQueue = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), k -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(tuple.getT1());
                    WenXinUtils.offerMessage(messageQueue, message);

                    BaseRequest baseRequest = BaseRequest.builder()
                            .messages(messageQueue)
                            .build();

                    log.info("{}content_contRequest => {}", getTag(), baseRequest.toString());

                    return historyMono(baseRequest, messageQueue);
                });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Mono.just(Tuples.of(content, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()) && StringUtils.isNotBlank(tuple.getT2()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .flatMapMany(tuple -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messageQueue = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), k -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(tuple.getT1());
                    WenXinUtils.offerMessage(messageQueue, message);

                    BaseRequest request = BaseRequest.builder()
                            .messages(messageQueue)
                            .stream(true)
                            .build();

                    log.info("{}content_contRequest_stream => {}", getTag(), request.toString());

                    return this.historyFlux(request, messageQueue);
                });
    }


    @Override
    public Mono<ChatResponse> chatCont(ChatBaseRequest chatBaseRequest, String msgUid) {
        return Mono.justOrEmpty(chatBaseRequest)
                .filter(request -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .flatMap(request -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            msgUid, key -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(request.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    BaseRequest baseRequest = ConvertUtils.toBaseRequest(request)
                            .messages(messagesHistory)
                            .build();

                    log.info("{}contRequest => {}", getTag(), baseRequest.toString());

                    return historyMono(baseRequest, messagesHistory);
                });
    }


    @Override
    public Flux<ChatResponse> chatContOfStream(ChatBaseRequest chatBaseRequest, String msgUid) {
        return Mono.justOrEmpty(chatBaseRequest)
                .filter(request -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .flatMapMany(request -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            msgUid, key -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(request.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    BaseRequest baseRequest = ConvertUtils.toBaseRequest(request)
                            .messages(messagesHistory)
                            .stream(true)
                            .build();

                    log.info("{}contRequest_stream => {}", getTag(), baseRequest.toString());

                    return this.historyFlux(baseRequest, messagesHistory);
                });
    }


    public <T> Flux<ChatResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            Flux<ChatResponse> chatResponse = ChatUtils.fluxChatPost(
                    getURL(), getCustomAccessToken(), request, ChatResponse.class
            );
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    public <T> Mono<ChatResponse> historyMono(T request, Queue<Message> messagesHistory) {
        Mono<ChatResponse> response = ChatUtils.monoChatPost(
                getURL(), getCustomAccessToken(), request, ChatResponse.class
        ).subscribeOn(Schedulers.boundedElastic());

        return response.flatMap(chatResponse -> {
            if (chatResponse == null || chatResponse.getResult() == null) {
                return Mono.error(new BusinessException(ErrorCode.SYSTEM_ERROR));
            }
            // 构建聊天响应消息
            Message messageResult = buildAssistantMessage(chatResponse.getResult());
            WenXinUtils.offerMessage(messagesHistory, messageResult);

            return Mono.just(chatResponse);
        });
    }

}