package com.gearwenxin.service;

import com.gearwenxin.common.*;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.core.WebManager;
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

    public static final String TAG = "ChatService";

    @Resource
    private WenXinProperties wenXinProperties;

    @Resource
    private MessageService messageService;

    private final WebManager webManager = new WebManager();

    private static final MessageHistoryManager messageHistoryManager = MessageHistoryManager.getInstance();

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    public <T extends ChatBaseRequest> Publisher<ChatResponse> chatProcess(T request, String msgUid, boolean stream, ModelConfig config) {
        validRequest(request, config);
        Map<String, Deque<Message>> messageMap = messageHistoryManager.getChatMessageHistoryMap();
        boolean isContinuous = (msgUid != null);
        String accessToken = config.getAccessToken() == null ? getAccessToken() : config.getAccessToken();
        Object targetRequest;
        if (isContinuous) {
            Deque<Message> messagesHistory = messageMap.computeIfAbsent(
                    msgUid, key -> new ArrayDeque<>()
            );
            targetRequest = buildTargetRequest(messagesHistory, stream, request);
            Message message = WenXinUtils.buildUserMessage(request.getContent());
            MessageHistoryManager.addMessage(messagesHistory, message);

            log.debug("[{}] stream: {}, continuous: {}", TAG, stream, true);

            return stream ? webManager.historyFluxPost(accessToken, targetRequest, messagesHistory, config) :
                    webManager.historyMonoPost(accessToken, targetRequest, messagesHistory, config, msgUid);
        } else {
            targetRequest = buildTargetRequest(null, stream, request);
        }

        log.debug("[{}] stream: {}, continuous: {}", TAG, stream, false);

        return stream ? webManager.fluxPost(config, accessToken, targetRequest, ChatResponse.class) :
                webManager.monoPost(config, accessToken, targetRequest, ChatResponse.class, msgUid);
    }

    public <T extends ChatBaseRequest> void validRequest(T request, ModelConfig config) {
        RequestValidator validator = RequestValidatorFactory.getValidator(config);
        validator.validate(request, config);
    }

    public static <T extends ChatBaseRequest> Object buildTargetRequest(Deque<Message> messagesHistory, boolean stream, T request) {
        Object targetRequest = null;
        if (request.getClass() == ChatBaseRequest.class) {
            BaseRequest.BaseRequestBuilder requestBuilder = ConvertUtils.toBaseRequest(request).stream(stream);
            if (messagesHistory != null) {
                requestBuilder.messages(messagesHistory);
            }
            targetRequest = requestBuilder.build();
        } else if (request.getClass() == ChatErnieRequest.class) {
            ErnieRequest.ErnieRequestBuilder requestBuilder = ConvertUtils.toErnieRequest((ChatErnieRequest) request).stream(stream);
            if (messagesHistory != null) {
                requestBuilder.messages(messagesHistory);
            }
            targetRequest = requestBuilder.build();
        }
        return targetRequest;
    }

}
