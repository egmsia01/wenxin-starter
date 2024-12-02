package com.gearwenxin.service;

import com.gearwenxin.common.*;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.core.RequestManager;
import com.gearwenxin.core.MessageHistoryManager;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.validator.RequestValidator;
import com.gearwenxin.validator.RequestValidatorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Service
public class ChatService {

    public static final String SERVICE_TAG = "ChatService";

    @Resource
    private WenXinProperties wenXinProperties;

    private final RequestManager requestManager = new RequestManager();

    private static final MessageHistoryManager messageHistoryManager = MessageHistoryManager.getInstance();

    private String retrieveAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    public <T extends ChatBaseRequest> Publisher<ChatResponse> processChatRequest(T request, String messageId,
                                                                                  boolean useStreaming,
                                                                                  ModelConfig modelConfig) {
        validateRequest(request, modelConfig);

        Map<String, Deque<Message>> chatHistoryMap = messageHistoryManager.getChatMessageHistoryMap();
        boolean hasHistory = (messageId != null);
        String accessToken = modelConfig.getAccessToken() == null
                ? retrieveAccessToken()
                : modelConfig.getAccessToken();

        Object targetRequest;

        if (hasHistory) {
            Deque<Message> messageHistory = chatHistoryMap.computeIfAbsent(
                    messageId, key -> new ArrayDeque<>()
            );
            targetRequest = prepareRequestWithHistory(messageHistory, useStreaming, request);
            Message userMessage = WenXinUtils.buildUserMessage(request.getContent());
            MessageHistoryManager.addMessage(messageHistory, userMessage);

            log.debug("[{}] Streaming: {}, Has History: {}", SERVICE_TAG, useStreaming, true);

            return useStreaming ?
                    requestManager.historyFluxPost(modelConfig, accessToken, targetRequest, messageHistory, messageId) :
                    requestManager.historyMonoPost(modelConfig, accessToken, targetRequest, messageHistory, messageId);
        } else {
            targetRequest = prepareRequestWithoutHistory(useStreaming, request);
        }

        log.debug("[{}] Streaming: {}, Has History: {}", SERVICE_TAG, useStreaming, false);

        return useStreaming ?
                requestManager.fluxPost(modelConfig, accessToken, targetRequest, ChatResponse.class, messageId) :
                requestManager.monoPost(modelConfig, accessToken, targetRequest, ChatResponse.class, messageId);
    }

    public <T extends ChatBaseRequest> void validateRequest(T request, ModelConfig modelConfig) {
        RequestValidator validator = RequestValidatorFactory.getValidator(modelConfig);
        validator.validate(request, modelConfig);
    }

    public static <T extends ChatBaseRequest> Object prepareRequestWithHistory(Deque<Message> messageHistory,
                                                                               boolean useStreaming, T request) {
        Object targetRequest = null;

        if (request.getClass() == ChatBaseRequest.class) {
            BaseRequest.BaseRequestBuilder requestBuilder = ConvertUtils.toBaseRequest(request).stream(useStreaming);
            if (messageHistory != null) {
                requestBuilder.messages(messageHistory);
            }
            targetRequest = requestBuilder.build();
        } else if (request.getClass() == ChatErnieRequest.class) {
            ErnieRequest.ErnieRequestBuilder requestBuilder = ConvertUtils.toErnieRequest(
                    (ChatErnieRequest) request).stream(useStreaming);
            if (messageHistory != null) {
                requestBuilder.messages(messageHistory);
            }
            targetRequest = requestBuilder.build();
        }

        return targetRequest;
    }

    public static <T extends ChatBaseRequest> Object prepareRequestWithoutHistory(
            boolean useStreaming, T request) {

        return prepareRequestWithHistory(null, useStreaming, request);
    }
}