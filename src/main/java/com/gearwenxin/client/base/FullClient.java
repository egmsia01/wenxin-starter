package com.gearwenxin.client.base;

import com.gearwenxin.common.*;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.chat.ContBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiFunction;

import static com.gearwenxin.common.WenXinUtils.assertNotBlankMono;
import static com.gearwenxin.common.WenXinUtils.buildTargetRequest;

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
        return Mono.from(this.chatContFunc(content, msgUid, this::chatCont));
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Flux.from(this.chatContFunc(content, msgUid, this::chatContOfStream));
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chatCont(T chatRequest, String msgUid) {
        return Mono.from(chatContProcess(chatRequest, msgUid, false));
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatContOfStream(T chatRequest, String msgUid) {
        return Flux.from(chatContProcess(chatRequest, msgUid, true));
    }

    public <T extends ChatBaseRequest> Publisher<ChatResponse> chatContProcess(T requestT, String msgUid, boolean stream) {
        return Mono.justOrEmpty(requestT)
                .filter(reqT -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(reqT -> validRequest(requestT))
                .flatMapMany(reqT -> {
                    Deque<Message> messagesHistory = getMessageHistoryMap().computeIfAbsent(
                            msgUid, key -> new ConcurrentLinkedDeque<>()
                    );
                    Message message = WenXinUtils.buildUserMessage(reqT.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    Object targetRequest = buildTargetRequest(messagesHistory, stream, reqT);

                    String logMessage = stream ? "{}-cont-request-stream => {}" : "{}-cont-request => {}";
                    log.info(logMessage, getTag(), targetRequest);

                    return typeReturnWithHistory(stream, targetRequest, messagesHistory);
                });
    }

    public Publisher<ChatResponse> typeReturnWithHistory(boolean stream, Object request, Deque<Message> messagesHistory) {
        return stream ? ChatUtils.historyFlux(getURL(), getCustomAccessToken(), request, messagesHistory) :
                ChatUtils.historyMono(getURL(), getCustomAccessToken(), request, messagesHistory);
    }

    private Publisher<ChatResponse> chatContFunc(String content, String msgUid, BiFunction<ChatBaseRequest, String, Publisher<ChatResponse>> chatFunction) {
        assertNotBlankMono(content, "content is null or blank");
        assertNotBlankMono(msgUid, "msgUid is null or blank");

        return chatFunction.apply(this.buildRequest(content), msgUid);
    }

    private ChatBaseRequest buildRequest(String content) {
        return ChatBaseRequest.builder().content(content).build();
    }

}