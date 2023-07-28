package com.gearwenxin.client;

import com.gearwenxin.model.chatmodel.ChatPromptRequest;
import com.gearwenxin.model.response.PromptResponse;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface Prompt {

    /**
     * 单独设置accessToken
     *
     * @param accessToken accessToken
     */
    void setAccessToken(String accessToken);

    /**
     * 获取模型的URL
     * @return URL
     */
    public String getURL();

    /**
     * Prompt模板对话 (Get请求 不支持流式返回)
     * （非流式）
     *
     * @param promptRequest 请求实体类
     * @return ChatResponse 响应实体类
     */
    PromptResponse chatPrompt(ChatPromptRequest promptRequest);

}
