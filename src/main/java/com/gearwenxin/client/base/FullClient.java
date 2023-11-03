package com.gearwenxin.client.base;

import com.gearwenxin.client.ernie.ErnieBotClient;
import com.gearwenxin.common.*;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.chat.ContBot;
import com.gearwenxin.service.ChatRequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Deque;
import java.util.function.BiFunction;

import static com.gearwenxin.common.WenXinUtils.assertNotBlankMono;

/**
 * @author Ge Mingjia
 * @date 2023/8/4
 */
@Slf4j
public abstract class FullClient extends BaseClient implements ContBot<ChatBaseRequest> {

    /**
     * 获取此模型的历史消息
     */
    public abstract Map<String, Deque<Message>> getMessageHistoryMap();

    /**
     * 初始化此模型的历史消息
     */
    public abstract void initMessageHistoryMap(Map<String, Deque<Message>> map);

    @Override
    public Mono<ChatResponse> chatCont(String content, String msgUid) {
        return Mono.defer(() -> Mono.from(chatContFunc(content, msgUid, this::chatCont)));
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Flux.defer(() -> chatContFunc(content, msgUid, this::chatContOfStream));
    }

    @Override
    public Mono<ChatResponse> chatCont(ChatBaseRequest chatBaseRequest, String msgUid) {
        return Mono.from(chatContProcess(chatBaseRequest, msgUid, false));
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatBaseRequest chatBaseRequest, String msgUid) {
        return Flux.from(chatContProcess(chatBaseRequest, msgUid, true));
    }

    private Publisher<ChatResponse> chatContProcess(ChatBaseRequest chatBaseRequest, String msgUid, boolean stream) {
        return Mono.justOrEmpty(chatBaseRequest)
                .filter(request -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .flatMapMany(request -> {
                    Deque<Message> messagesHistory = getMessageHistoryMap().computeIfAbsent(
                            msgUid, key -> new LinkedList<>()
                    );

                    Message message = WenXinUtils.buildUserMessage(request.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    BaseRequest baseRequest = ConvertUtils.toBaseRequest(request)
                            .messages(messagesHistory)
                            .stream(stream)
                            .build();

                    String logMessage = stream ? "{}-contRequest-stream => {}" : "{}-contRequest => {}";
                    log.info(logMessage, getTag(), baseRequest);

                    if (stream) {
                        return ChatUtils.historyFlux(getURL(), getCustomAccessToken(), baseRequest, messagesHistory);
                    } else {
                        return ChatUtils.historyMono(getURL(), getCustomAccessToken(), baseRequest, messagesHistory);
                    }
                });
    }

    private Publisher<ChatResponse> chatContFunc(String content, String msgUid, BiFunction<ChatBaseRequest, String, Publisher<ChatResponse>> chatFunction) {
        assertNotBlankMono(content, "content is null or blank");
        assertNotBlankMono(msgUid, "msgUid is null or blank");

        return chatFunction.apply(buildBaseRequest(content), msgUid);
    }

    private ChatBaseRequest buildBaseRequest(String content) {
        return ChatBaseRequest.builder().content(content).build();
    }

    private <R extends ChatBaseRequest> Publisher<ChatResponse> processRequestR(R requestR, String msgUid, boolean stream, ChatRequestProcessor<R> processor) {
        return Mono.justOrEmpty(requestR)
                .filter(request -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(request -> {
                    if (request.getClass() == ChatBaseRequest.class) {
                        ChatBaseRequest.validSelf(request);
                    } else if (request.getClass() == ChatErnieRequest.class) {
                        ErnieBotClient.validChatErnieRequest((ChatErnieRequest) request);
                    }
                })
                .flatMapMany(request -> {
                    Deque<Message> messagesHistory = getMessageHistoryMap().computeIfAbsent(
                            msgUid, key -> new LinkedList<>()
                    );
                    Message message = WenXinUtils.buildUserMessage(request.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    Object baseRequest = null;
                    if (request.getClass() == ChatBaseRequest.class) {
                        baseRequest = ConvertUtils.toBaseRequest(request)
                                .messages(messagesHistory)
                                .stream(stream)
                                .build();
                    } else if (request.getClass() == ChatErnieRequest.class) {
                        baseRequest = ConvertUtils.toErnieRequest((ChatErnieRequest) request)
                                .messages(messagesHistory)
                                .stream(stream)
                                .build();
                    }

                    String logMessage = stream ? "{}-contRequest-stream => {}" : "{}-contRequest => {}";
                    log.info(logMessage, getTag(), baseRequest);

                    return processor.process(request, msgUid, stream);
                });
    }


}