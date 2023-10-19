package com.gearwenxin.service;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.entity.BaseProperty;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/9/24
 */
@Slf4j
@Service
public class ErnieService {
    public Flux<ChatResponse> chatSingleOfStream(String content, BaseProperty baseProperty) {
        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> ErnieRequest.builder().messages(messageDeque).stream(true).build())
                .doOnNext(request -> log.info("{}-content_singleRequest_stream => {}", baseProperty.getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(baseProperty.getUrl(), baseProperty.getAccessToken(), request, ChatResponse.class)
                );

    }

    public Mono<ChatResponse> chatSingle(ChatErnieRequest chatBaseRequest, BaseProperty baseProperty) {
        return Mono.justOrEmpty(chatBaseRequest)
                .doOnNext(ChatErnieRequest::validSelf)
                .map(ConvertUtils::toErnieRequest)
                .map(BaseRequest.BaseRequestBuilder::build)
                .doOnNext(request -> log.info("{}-singleRequest => {}", baseProperty.getTag(), request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(baseProperty.getUrl(), baseProperty.getAccessToken(), request, ChatResponse.class)
                );

    }

    public Flux<ChatResponse> chatSingleOfStream(ChatErnieRequest chatBaseRequest, BaseProperty baseProperty) {

        return Mono.justOrEmpty(chatBaseRequest)
                .doOnNext(ChatErnieRequest::validSelf)
                .map(ConvertUtils::toErnieRequest)
                .map(builder -> builder.stream(true).build())
                .doOnNext(request -> log.info("{}-singleRequest_stream => {}", baseProperty.getTag(), request.toString()))
                .flatMapMany(request ->
                        ChatUtils.fluxChatPost(baseProperty.getUrl(), baseProperty.getAccessToken(), request, ChatResponse.class)
                );
    }

    public Mono<ChatResponse> chatSingle(String content, BaseProperty baseProperty) {

        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> ErnieRequest.builder().messages(messageDeque).build())
                .doOnNext(request -> log.info("{}-content_singleRequest => {}", baseProperty.getUrl(), request.toString()))
                .flatMap(request ->
                        ChatUtils.monoChatPost(baseProperty.getUrl(), baseProperty.getAccessToken(), request, ChatResponse.class)
                );
    }
}
