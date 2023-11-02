package com.gearwenxin.client.base;

import com.gearwenxin.common.*;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.chat.ContBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Deque;

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
        return Mono.defer(() -> {
            if (StringUtils.isBlank(content) || StringUtils.isBlank(msgUid)) {
                return Mono.error(new WenXinException(ErrorCode.PARAMS_ERROR, "content or msgUid is null"));
            }

            ChatBaseRequest chatBaseRequest = ChatBaseRequest.builder()
                    .content(content)
                    .build();

            return chatCont(chatBaseRequest, msgUid);
        });
    }

    @Override
    public Mono<ChatResponse> chatCont(ChatBaseRequest chatBaseRequest, String msgUid) {
        return Mono.justOrEmpty(chatBaseRequest)
                .filter(request -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .flatMap(request -> {
                    Deque<Message> messagesHistory = getMessageHistoryMap().computeIfAbsent(
                            msgUid, key -> new LinkedList<>()
                    );

                    Message message = WenXinUtils.buildUserMessage(request.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    BaseRequest baseRequest = ConvertUtils.toBaseRequest(request)
                            .messages(messagesHistory)
                            .build();

                    log.info("{}-contRequest => {}", getTag(), baseRequest.toString());

                    return ChatUtils.historyMono(getURL(), getCustomAccessToken(), baseRequest, messagesHistory);
                });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Flux.defer(() -> {
            if (StringUtils.isBlank(content) || StringUtils.isBlank(msgUid)) {
                return Flux.error(new WenXinException(ErrorCode.PARAMS_ERROR, "content or msgUid is null"));
            }

            ChatBaseRequest chatBaseRequest = ChatBaseRequest.builder()
                    .content(content)
                    .build();

            return chatCont(chatBaseRequest, msgUid);
        });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatBaseRequest chatBaseRequest, String msgUid) {
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
                            .stream(true)
                            .build();

                    log.info("{}-contRequest-stream => {}", getTag(), baseRequest.toString());

                    return ChatUtils.historyFlux(getURL(), getCustomAccessToken(), baseRequest, messagesHistory);
                });
    }

}