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
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.SingleBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;

/**
 * @author Ge Mingjia
 * @date 2023/8/4
 */
@Slf4j
public abstract class BaseClient implements SingleBot, BaseBot {

    private Mono<BaseRequest> buildBaseRequest(String content) {
        return Mono.justOrEmpty(content)
                .filter(StringUtils::isNotBlank)
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .map(WenXinUtils::buildUserMessageDeque)
                .map(messageDeque -> BaseRequest.builder().messages(messageDeque).build());
    }

    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        switch (getTag()) {
            case "ErnieBotClient", "ErnieBot4Client", "ErnieBotTurboClient" -> {
                return buildBaseRequest(content)
                        .map(request -> ErnieRequest.builder().messages(request.getMessages()).build())
                        .doOnNext(request -> log.info("{}-content-singleRequest => {}", getTag(), request.toString()))
                        .flatMap(request ->
                                ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
            default -> {
                return buildBaseRequest(content)
                        .doOnNext(request -> log.info("{}-content-singleRequest => {}", getTag(), request.toString()))
                        .flatMap(request ->
                                ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
        }
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        switch (getTag()) {
            case "ErnieBotClient", "ErnieBot4Client", "ErnieBotTurboClient" -> {
                return buildBaseRequest(content)
                        .map(request -> ErnieRequest.builder().messages(request.getMessages()).stream(true).build())
                        .doOnNext(request -> log.info("{}-content-singleRequest-stream => {}", getTag(), request.toString()))
                        .flatMapMany(request ->
                                ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
            default -> {
                return buildBaseRequest(content)
                        .map(request -> BaseRequest.builder().messages(request.getMessages()).stream(true).build())
                        .doOnNext(request -> log.info("{}-content-singleRequest-stream => {}", getTag(), request.toString()))
                        .flatMapMany(request ->
                                ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
        }
    }


    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingle(T chatBaseRequest) {
        switch (getTag()) {
            case "ErnieBotClient", "ErnieBot4Client", "ErnieBotTurboClient" -> {
                return Mono.justOrEmpty((ChatErnieRequest) chatBaseRequest)
                        .doOnNext(ErnieBotClient::validChatErnieRequest)
                        .map(ConvertUtils::toErnieRequest)
                        .map(BaseRequest.BaseRequestBuilder::build)
                        .doOnNext(request -> log.info("{}-singleRequest => {}", getTag(), request.toString()))
                        .flatMap(request ->
                                ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
            default -> {
                return Mono.just(chatBaseRequest)
                        .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                        .doOnNext(BaseClient::validChatRequest)
                        .map(ConvertUtils::toBaseRequest)
                        .map(BaseRequest.BaseRequestBuilder::build)
                        .doOnNext(baseRequest -> log.info("{}-singleRequest => {}", getTag(), baseRequest.toString()))
                        .flatMap(baseRequest ->
                                ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class)
                        );
            }
        }

    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatSingleOfStream(T chatBaseRequest) {
        switch (getTag()) {
            case "ErnieBotClient", "ErnieBot4Client", "ErnieBotTurboClient" -> {
                return Mono.justOrEmpty((ChatErnieRequest) chatBaseRequest)
                        .doOnNext(ErnieBotClient::validChatErnieRequest)
                        .map(ConvertUtils::toErnieRequest)
                        .map(builder -> builder.stream(true).build())
                        .doOnNext(request -> log.info("{}-singleRequest-stream => {}", getTag(), request.toString()))
                        .flatMapMany(request ->
                                ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
                        );
            }
            default -> {
                return Mono.just(chatBaseRequest)
                        .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                        .doOnNext(BaseClient::validChatRequest)
                        .map(ConvertUtils::toBaseRequest)
                        .map(builder -> builder.stream(true).build())
                        .doOnNext(baseRequest -> log.info("{}-singleRequest-stream => {}", getTag(), baseRequest.toString()))
                        .flatMapMany(baseRequest ->
                                ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class)
                        );
            }
        }

    }

    public static void validChatRequest(ChatBaseRequest chatBaseRequest) {

        // 检查content不为空
        if (StringUtils.isBlank(chatBaseRequest.getContent())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (chatBaseRequest.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
    }

}