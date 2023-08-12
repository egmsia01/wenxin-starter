package com.gearwenxin.model;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface DefaultBot<T extends ChatBaseRequest> {

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
    Mono<ChatResponse> chatSingle(T chatRequest);

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （流式）
     *
     * @param chatRequest 请求实体类
     * @return ChatResponse Flux<ChatResponse>
     */
    Flux<ChatResponse> chatSingleOfStream(T chatRequest);

    /**
     * 多轮对话，有上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @param msgUid  对话唯一识别码
     * @return ChatResponse 响应实体类
     */
    Mono<ChatResponse> chatCont(String content, String msgUid);

    /**
     * 多轮对话，有上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @param msgUid  对话唯一识别码
     * @return ChatResponse 响应实体类
     */
    Flux<ChatResponse> chatContOfStream(String content, String msgUid);

    /**
     * 多轮对话，有上下文记忆，支持参数配置
     * （非流式）
     *
     * @param chatRequest 请求实体类
     * @param msgUid      对话唯一识别码
     * @return ChatResponse 响应实体类
     */
    Mono<ChatResponse> chatCont(T chatRequest, String msgUid);

    /**
     * 多轮对话，有上下文记忆，支持参数配置
     * （非流式）
     *
     * @param chatRequest 请求实体类
     * @param msgUid      对话唯一识别码
     * @return ChatResponse 响应实体类
     */
    Flux<ChatResponse> chatContOfStream(T chatRequest, String msgUid);

}
