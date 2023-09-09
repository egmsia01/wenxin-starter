package com.gearwenxin.client.ernie;

import com.gearwenxin.common.*;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.ContBot;
import com.gearwenxin.model.SingleBot;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.WenXinUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotClient implements SingleBot<ChatErnieRequest>, ContBot<ChatErnieRequest>, BaseBot {

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
    public Mono<ChatResponse> chatSingle(String content) {
        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageQueue)
                .map(messageQueue -> ErnieRequest.builder().messages(messageQueue).build())
                .doOnNext(request -> log.info("{}content_singleRequest => {}", getTag(), request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageQueue)
                .map(messageQueue -> ErnieRequest.builder().messages(messageQueue).stream(true).build())
                .doOnNext(request -> log.info("{}content_singleRequest_stream => {}", getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }

    @Override
    public Mono<ChatResponse> chatSingle(ChatErnieRequest chatErnieRequest) {
        return Mono.justOrEmpty(chatErnieRequest)
                .doOnNext(this::validChatErnieRequest)
                .map(ConvertUtils::toErnieRequest)
                .map(BaseRequest.BaseRequestBuilder::build)
                .doOnNext(request -> log.info("{}singleRequest => {}", getTag(), request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatErnieRequest chatErnieRequest) {
        return Mono.justOrEmpty(chatErnieRequest)
                .doOnNext(this::validChatErnieRequest)
                .map(ConvertUtils::toErnieRequest)
                .map(builder -> builder.stream(true).build())
                .doOnNext(request -> log.info("{}singleRequest_stream => {}", getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }

    @Override
    public Mono<ChatResponse> chatCont(String content, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(content, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()) && StringUtils.isNotBlank(tuple.getT2()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .flatMap(tuple -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messageQueue = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), k -> new LinkedList<>()
                    );
                    Message message = buildUserMessage(tuple.getT1());
                    WenXinUtils.offerMessage(messageQueue, message);

                    ErnieRequest ernieRequest = ErnieRequest.builder()
                            .messages(messageQueue)
                            .build();

                    log.info("{}content_contRequest => {}", getTag(), ernieRequest.toString());

                    return this.historyMono(ernieRequest, messageQueue);
                });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(content, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()) && StringUtils.isNotBlank(tuple.getT2()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .flatMapMany(tuple -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), k -> new LinkedList<>()
                    );
                    Message message = buildUserMessage(tuple.getT1());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ErnieRequest.builder()
                            .messages(messagesHistory)
                            .stream(true)
                            .build();

                    log.info("{}content_contRequest_stream => {}", getTag(), ernieRequest.toString());

                    return this.historyFlux(ernieRequest, messagesHistory);
                });
    }

    @Override
    public Mono<ChatResponse> chatCont(ChatErnieRequest chatErnieRequest, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(chatErnieRequest, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT2()))
                .doOnNext(tuple -> this.validChatErnieRequest(tuple.getT1()))
                .flatMap(tuple -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), key -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(tuple.getT1().getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ConvertUtils.toErnieRequest(tuple.getT1())
                            .messages(messagesHistory)
                            .build();

                    log.info("{}contRequest => {}", getTag(), ernieRequest.toString());

                    return this.historyMono(ernieRequest, messagesHistory);
                });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatErnieRequest chatErnieRequest, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(chatErnieRequest, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT2()))
                .doOnNext(tuple -> this.validChatErnieRequest(tuple.getT1()))
                .flatMapMany(tuple -> {
                    Map<String, Queue<Message>> messageHistoryMap = getMessageHistoryMap();
                    Queue<Message> messageQueue = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), key -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(tuple.getT1().getContent());
                    WenXinUtils.offerMessage(messageQueue, message);

                    ErnieRequest ernieRequest = ConvertUtils.toErnieRequest(tuple.getT1())
                            .messages(messageQueue)
                            .stream(true)
                            .build();

                    log.info("{}contRequest_stream => {}", getTag(), ernieRequest.toString());

                    return this.historyFlux(ernieRequest, messageQueue);
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
            if (chatResponse == null) {
                return Mono.error(new BusinessException(ErrorCode.SYSTEM_NET_ERROR));
            }
            // 构建聊天响应消息
            Message messageResult = buildAssistantMessage(chatResponse.getResult());
            WenXinUtils.offerMessage(messagesHistory, messageResult);

            return Mono.just(chatResponse);
        });
    }

    public void validChatErnieRequest(ChatErnieRequest request) {

        // 检查content不为空
        if (StringUtils.isBlank(request.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
        // 检查temperature和topP不都有值
        if (request.getTemperature() != null && request.getTopP() != null) {
            log.warn("Temperature and topP cannot both have value");
        }
        // 检查temperature范围
        if (request.getTemperature() != null && (request.getTemperature() <= 0 || request.getTemperature() > 1.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "temperature should be in (0, 1]");
        }
        // 检查topP范围
        if (request.getTopP() != null && (request.getTopP() < 0 || request.getTopP() > 1.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "topP should be in [0, 1]");
        }
        // 检查penaltyScore范围
        if (request.getTemperature() != null && (request.getPenaltyScore() < 1.0 || request.getPenaltyScore() > 2.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "penaltyScore should be in [1, 2]");
        }

    }
}
