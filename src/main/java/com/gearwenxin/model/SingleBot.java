package com.gearwenxin.model;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface SingleBot {

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @return ChatResponse 响应实体类
     */
    Mono<ChatResponse> chatSingle(String content);

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （流式）
     *
     * @param content 对话内容
     * @return ChatResponse Flux<ChatResponse>
     */
    Flux<ChatResponse> chatSingleOfStream(String content);

    /**
     * 单轮对话，无上下文记忆，支持参数配置
     * （非流式）
     *
     * @param chatRequest 请求实体类
     * @return ChatResponse 响应实体类
     */
    <T extends ChatBaseRequest> Mono<ChatResponse> chatSingle(T chatRequest);

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （流式）
     *
     * @param chatRequest 请求实体类
     * @return ChatResponse Flux<ChatResponse>
     */
    <T extends ChatBaseRequest> Flux<ChatResponse> chatSingleOfStream(T chatRequest);

}
