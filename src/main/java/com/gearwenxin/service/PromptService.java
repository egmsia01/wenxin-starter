package com.gearwenxin.service;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.core.WebManager;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.WenXinException;

import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.PromptModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Lazy
@Service
public class PromptService {

    public static final String TAG = "PromptService";

    private final WebManager webManager = new WebManager();

    @Resource
    private WenXinProperties wenXinProperties;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    public Mono<PromptResponse> chatPromptProcess(ChatPromptRequest chatPromptRequest, ModelConfig config) {
        if (chatPromptRequest == null || chatPromptRequest.getId() <= 0 || CollectionUtils.isEmpty(chatPromptRequest.getParamMap())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "chatPromptRequest is null or id is null or paramMap is null");
        }
        PromptRequest promptRequest = ConvertUtils.toPromptRequest(chatPromptRequest);
        Map<String, String> paramMap = promptRequest.getParamMap();
        paramMap.put("id", promptRequest.getId());

        return webManager.monoGet(config, getAccessToken(), paramMap, PromptResponse.class);
    }

    public <U extends ChatService, T extends ChatBaseRequest> Flux<ChatResponse> chatUsePrompt(ChatPromptRequest request, T chatRequest, U chatClient, ModelConfig config) {
        return this.chatPromptProcess(request, config).flatMapMany(response -> {
            log.debug("PromptResponse => {}", response);
            chatRequest.setContent(response.getResult().getContent());

//            return chatClient.chatProcess(chatRequest, null);
            return null;
        });
    }

}
