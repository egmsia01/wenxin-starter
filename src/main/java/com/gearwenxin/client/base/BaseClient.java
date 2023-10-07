package com.gearwenxin.client.base;

import com.gearwenxin.client.ernie.ErnieBotClient;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.SingleBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ge Mingjia
 * @date 2023/8/4
 */
@Slf4j
public abstract class BaseClient implements SingleBot, BaseBot {

    private static final Map<String, Function<String, Mono<ChatResponse>>> clientMapMono = new HashMap<>();
    private static final Map<String, Function<String, Flux<ChatResponse>>> clientMapFlux = new HashMap<>();

    public void initClient() {
        clientMapMono.put("ErnieBotClientCS", this::chatSingleErnie);
        clientMapMono.put("OtherClientCS", this::chatSingleDefult);
        clientMapFlux.put("ErnieBotClientCSS", this::chatSingleOfStreamErnie);
        clientMapFlux.put("OtherClientCSS", this::chatSingleOfStreamDefault);
    }

    @Deprecated
    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        Function<String, Mono<ChatResponse>> fluxFunction = clientMapMono.get(getTag() + "CS");
        if (fluxFunction != null) {
            return fluxFunction.apply(content);
        }
        return Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }

    @Deprecated
    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        Function<String, Flux<ChatResponse>> fluxFunction = clientMapFlux.get(getTag() + "CSS");
        if (fluxFunction != null) {
            return fluxFunction.apply(content);
        }
        return Flux.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingle(T chatBaseRequest) {
        Function<String, Mono<ChatResponse>> fluxFunction = clientMapMono.get(getTag() + "CS");
        if (fluxFunction != null) {
//            return fluxFunction.apply(chatBaseRequest);
        }
        return Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }

    public Flux<ChatResponse> chatSingleOfStreamDefault(String content) {
        return Mono.just(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> BaseRequest.builder().messages(messageDeque).stream(true).build())
                .doOnNext(request -> log.info("{}-content_singleRequest_stream => {}", getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );

    }

    public Flux<ChatResponse> chatSingleOfStreamErnie(String content) {
        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> ErnieRequest.builder().messages(messageDeque).stream(true).build())
                .doOnNext(request -> log.info("{}-content_singleRequest_stream => {}", getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );


    }

    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingleDefault(T chatBaseRequest) {

        return Mono.just(chatBaseRequest)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ChatBaseRequest::validSelf)
                .map(ConvertUtils::toBaseRequest)
                .map(BaseRequest.BaseRequestBuilder::build)
                .doOnNext(baseRequest -> log.info("{}-singleRequest => {}", getTag(), baseRequest.toString()))
                .flatMap(baseRequest ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class)
                );
    }

    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingleErnie(T chatBaseRequest) {
        return Mono.justOrEmpty((ChatErnieRequest) chatBaseRequest)
                .doOnNext(ErnieBotClient::validChatErnieRequest)
                .map(ConvertUtils::toErnieRequest)
                .map(BaseRequest.BaseRequestBuilder::build)
                .doOnNext(request -> log.info("{}-singleRequest => {}", getTag(), request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );

    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatSingleOfStream(T chatBaseRequest) {
        switch (getTag()) {
            case "ErnieBotClient" -> {
                return Mono.justOrEmpty((ChatErnieRequest) chatBaseRequest)
                        .doOnNext(ErnieBotClient::validChatErnieRequest)
                        .map(ConvertUtils::toErnieRequest)
                        .map(builder -> builder.stream(true).build())
                        .doOnNext(request -> log.info("{}-singleRequest_stream => {}", getTag(), request.toString()))
                        .flatMapMany(request ->
                                ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
            default -> {
                return Mono.just(chatBaseRequest)
                        .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                        .doOnNext(ChatBaseRequest::validSelf)
                        .map(ConvertUtils::toBaseRequest)
                        .map(builder -> builder.stream(true).build())
                        .doOnNext(baseRequest -> log.info("{}-singleRequest_stream => {}", getTag(), baseRequest.toString()))
                        .flatMapMany(baseRequest ->
                                ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class)
                        );
            }
        }

    }

    public Mono<ChatResponse> chatSingleErnie(String content) {

        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> ErnieRequest.builder().messages(messageDeque).build())
                .doOnNext(request -> log.info("{}-content_singleRequest => {}", getTag(), request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );
    }

    public Mono<ChatResponse> chatSingleDefult(String content) {

        return Mono.just(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> BaseRequest.builder().messages(messageDeque).build())
                .doOnNext(request -> log.info(getTag() + "content_singleRequest => {}", request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                );

    }

}