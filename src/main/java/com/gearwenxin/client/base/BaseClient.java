package com.gearwenxin.client.base;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.BaseProperty;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.enums.ChatType;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.SingleBot;
import com.gearwenxin.service.DefaultService;
import com.gearwenxin.service.ErnieService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    private BaseProperty baseProperty = null;

    @Resource
    private DefaultService defaultService;

    @Resource
    private ErnieService ernieService;

    public void initClient() {
        baseProperty = BaseProperty.builder()
                .url(getURL())
                .tag(getTag())
                .accessToken(getCustomAccessToken())
                .build();
        clientMapMono.put("ErnieBotClient" + ChatType.once, this::chatSingleErnie);
        clientMapMono.put("OtherClient" + ChatType.once, this::chatSingleDefault);
        clientMapFlux.put("ErnieBotClient" + ChatType.contStream, this::chatSingleOfStreamErnie);
        clientMapFlux.put("OtherClient" + ChatType.contStream, this::chatSingleOfStreamDefault);
    }

    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        Function<String, Mono<ChatResponse>> fluxFunction = clientMapMono.get(getTag() + ChatType.once);
        if (fluxFunction != null) {
            return fluxFunction.apply(content);
        }
        return Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {

        Function<String, Flux<ChatResponse>> fluxFunction = clientMapFlux.get(getTag() + ChatType.contStream);
        if (fluxFunction != null) {
            return fluxFunction.apply(content);
        }
        return Flux.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingle(T chatBaseRequest) {
        Function<String, Mono<ChatResponse>> fluxFunction = clientMapMono.get(getTag() + ChatType.once);
        if (fluxFunction != null) {
//            return fluxFunction.apply(chatBaseRequest);
        }
        return Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatSingleOfStream(T chatBaseRequest) {
        Function<String, Flux<ChatResponse>> fluxFunction = clientMapFlux.get(getTag() + ChatType.contStream);
        if (fluxFunction != null) {
//            return fluxFunction.apply(chatBaseRequest);
        }
        return Flux.error(new BusinessException(ErrorCode.PARAMS_ERROR));
    }


    public Flux<ChatResponse> chatSingleOfStreamDefault(String content) {
        return defaultService.chatSingleOfStream(content, baseProperty);
    }

    public Flux<ChatResponse> chatSingleOfStreamErnie(String content) {
        return ernieService.chatSingleOfStream(content, baseProperty);
    }

    public Mono<ChatResponse> chatSingleDefault(ChatBaseRequest chatBaseRequest) {
        return defaultService.chatSingle(chatBaseRequest, baseProperty);
    }

    public Mono<ChatResponse> chatSingleErnie(ChatErnieRequest chatErnieRequest) {
        return ernieService.chatSingle(chatErnieRequest, baseProperty);
    }

    public Mono<ChatResponse> chatSingleErnie(String content) {
        return ernieService.chatSingle(content, baseProperty);
    }

    public Mono<ChatResponse> chatSingleDefault(String content) {
        return defaultService.chatSingle(content, baseProperty);
    }

}