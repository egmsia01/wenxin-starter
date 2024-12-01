package com.gearwenxin.service;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.core.RequestManager;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.exception.WenXinException;

import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Service
public class PromptService {

    private final RequestManager requestManager = new RequestManager();

    @Resource
    private WenXinProperties wenXinProperties;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    public Mono<PromptResponse> promptProcess(ChatPromptRequest chatPromptRequest, ModelConfig config) {
        if (chatPromptRequest == null || chatPromptRequest.getId() == null || CollectionUtils.isEmpty(chatPromptRequest.getParamMap())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "chatPromptRequest is null or id is null or paramMap is null");
        }
        PromptRequest promptRequest = ConvertUtils.toPromptRequest(chatPromptRequest);
        Map<String, String> paramMap = promptRequest.getParamMap();
        paramMap.put("id", promptRequest.getId());

        return requestManager.monoGet(config, getAccessToken(), paramMap, PromptResponse.class);
    }

}
