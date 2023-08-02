package com.gearwenxin.client;

import com.gearwenxin.model.Message;
import com.gearwenxin.model.response.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Queue;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface CommonBot<T> {

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @return ChatResponse 响应实体类
     */
    ChatResponse chatSingle(String content);

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
    ChatResponse chatSingle(T chatRequest);

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
    ChatResponse chatCont(String content, String msgUid);

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
    ChatResponse chatCont(T chatRequest, String msgUid);

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
