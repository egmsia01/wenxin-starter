package com.gearwenxin.model;

import com.gearwenxin.service.ChatService;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.PromptResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
public interface PromptModel {

    /**
     * Prompt模板对话 (Get请求 不支持流式返回)
     * （非流式）
     *
     * @param request 请求实体类
     * @return ChatResponse 响应实体类
     */
    Mono<PromptResponse> chat(ChatPromptRequest request);
    Mono<PromptResponse> chat(ChatPromptRequest request, float weight);


}
