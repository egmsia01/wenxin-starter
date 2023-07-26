package com.gearwenxin.client;

import com.gearwenxin.model.request.PromptRequest;
import com.gearwenxin.model.response.ChatResponse;
import com.gearwenxin.model.response.PromptResponse;
import com.gearwenxin.model.response.PromptResult;
import reactor.core.publisher.Flux;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface PromptBot {

    /**
     * 获取 accessToken
     *
     * @return accessToken
     */
    String getAccessToken();

    /**
     * Prompt模板对话 (Get请求 不支持流式返回)
     * （非流式）
     *
     * @param promptRequest 请求实体类
     * @return ChatResponse 响应实体类
     */
    PromptResponse chatPrompt(PromptRequest promptRequest);

}
