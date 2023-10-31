package com.gearwenxin.model;

import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia

 */
public interface PromptBot {

    /**
     * Prompt模板对话 (Get请求 不支持流式返回)
     * （非流式）
     *
     * @param request 请求实体类
     * @return ChatResponse 响应实体类
     */
    Mono<PromptResponse> chatPrompt(ChatPromptRequest request);

}
