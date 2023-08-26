package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.DefaultBot;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Queue;

import static com.gearwenxin.common.WenXinUtils.*;

@Slf4j
public abstract class BaseClient implements DefaultBot<ChatBaseRequest> {

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
        if (content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);

        BaseRequest request = BaseRequest.builder()
                .messages(messageQueue)
                .build();

        log.info(getTag() + "content_singleRequest => {}", request.toString());

        return ChatUtils.monoChatPost(
                getURL(), getCustomAccessToken(), request, ChatResponse.class
        );
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        if (content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);

        BaseRequest baseRequest = BaseRequest.builder()
                .messages(messageQueue)
                .stream(true)
                .build();

        log.info("{}content_singleRequest_stream => {}", getTag(), baseRequest.toString());

        return ChatUtils.fluxChatPost(
                getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class
        );
    }

    @Override
    public Mono<ChatResponse> chatSingle(ChatBaseRequest chatBaseRequest) {
        if (chatBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chatBaseRequest.validSelf();
        BaseRequest baseRequest = ConvertUtils.toBaseRequest(chatBaseRequest).build();

        log.info("{}singleRequest => {}", getTag(), baseRequest.toString());

        return ChatUtils.monoChatPost(
                getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class
        );
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(ChatBaseRequest chatBaseRequest) {
        if (chatBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chatBaseRequest.validSelf();

        BaseRequest baseRequest = ConvertUtils.toBaseRequest(chatBaseRequest)
                .stream(true)
                .build();

        log.info("{}singleRequest_stream => {}", getTag(), baseRequest.toString());

        return ChatUtils.fluxChatPost(
                getURL(), getCustomAccessToken(), baseRequest, ChatResponse.class
        );
    }

}