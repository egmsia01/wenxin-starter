package com.gearwenxin.client.base;

import com.gearwenxin.client.ernie.ErnieBotClient;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.SingleBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.WenXinUtils.assertNotBlankMono;
import static com.gearwenxin.common.WenXinUtils.buildTargetRequest;

/**
 * @author Ge Mingjia
 * @date 2023/8/4
 */
@Slf4j
public abstract class BaseClient implements SingleBot, BaseBot {

    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        return Mono.from(this.chatSingleFunc(content, this::chatSingle));
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        return Flux.from(this.chatSingleFunc(content, this::chatSingleOfStream));
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingle(T chatRequest) {
        return Mono.from(chatSingleProcess(chatRequest, false));
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatSingleOfStream(T chatRequest) {
        return Flux.from(chatSingleProcess(chatRequest, true));
    }

    public <T extends ChatBaseRequest> Publisher<ChatResponse> chatSingleProcess(T requestT, boolean stream) {
        return Mono.justOrEmpty(requestT)
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(reqT -> validRequest(requestT))
                .flatMapMany(reqT -> {
                    Object targetRequest = buildTargetRequest(null, stream, reqT);

                    String logMessage = stream ? "{}-singleRequest-stream => {}" : "{}-singleRequest => {}";
                    log.info(logMessage, getTag(), targetRequest);

                    return typeReturn(stream, targetRequest);
                });
    }

    public Publisher<ChatResponse> typeReturn(boolean stream, Object request) {
        return stream ? ChatUtils.fluxChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class) :
                ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class);
    }

    public <T extends ChatBaseRequest> void validRequest(T request) {
        if (request.getClass() == ChatBaseRequest.class) {
            BaseClient.validChatRequest(request);
        } else if (request.getClass() == ChatErnieRequest.class) {
            ErnieBotClient.validChatErnieRequest((ChatErnieRequest) request);
        }
    }

//    public <T extends ChatBaseRequest> void validRequest(T request) {
//        RequestValidator<T> validator;
//        if (request.getClass() == ChatBaseRequest.class) {
//            validator = new ChatBaseRequestValidator();
//        } else if (request.getClass() == ChatErnieRequest.class) {
//            validator = new ChatErnieRequestValidator();
//        } else {
//            throw new IllegalArgumentException("Unsupported request type");
//        }
//        validator.validate(request);
//    }

    private Publisher<ChatResponse> chatSingleFunc(String content, Function<ChatBaseRequest, Publisher<ChatResponse>> chatFunction) {
        assertNotBlankMono(content, "content is null or blank");

        return chatFunction.apply(this.buildRequest(content));
    }

    private ChatBaseRequest buildRequest(String content) {
        return ChatBaseRequest.builder().content(content).build();
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