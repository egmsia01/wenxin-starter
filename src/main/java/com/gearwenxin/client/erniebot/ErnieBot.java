package com.gearwenxin.client.erniebot;

import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ErnieResponse;
import reactor.core.publisher.Flux;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ErnieBot {

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatSingle(String content);

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （流式）
     *
     * @param content 对话内容
     * @return ErnieResponse Flux<ErnieResponse>
     */
    Flux<ErnieResponse> chatSingleOfStream(String content);

    /**
     * 单轮对话，无上下文记忆，支持参数配置
     * （非流式）
     *
     * @param chatErnieRequest 请求实体类
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatSingle(ChatErnieRequest chatErnieRequest);

    /**
     * 单轮对话，无上下文记忆，默认参数
     * （流式）
     *
     * @param chatErnieRequest 请求实体类
     * @return ErnieResponse Flux<ErnieResponse>
     */
    Flux<ErnieResponse> chatSingleOfStream(ChatErnieRequest chatErnieRequest);

    /**
     * 多轮对话，有上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @param msgUid  对话唯一识别码
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatCont(String content, String msgUid);

    /**
     * 多轮对话，有上下文记忆，默认参数
     * （非流式）
     *
     * @param content 对话内容
     * @param msgUid  对话唯一识别码
     * @return ErnieResponse 响应实体类
     */
    Flux<ErnieResponse> chatContOfStream(String content, String msgUid);

    /**
     * 多轮对话，有上下文记忆，支持参数配置
     * （非流式）
     *
     * @param chatErnieRequest 请求实体类
     * @param msgUid           对话唯一识别码
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatCont(ChatErnieRequest chatErnieRequest, String msgUid);

    /**
     * 多轮对话，有上下文记忆，支持参数配置
     * （非流式）
     *
     * @param chatErnieRequest 请求实体类
     * @param msgUid           对话唯一识别码
     * @return ErnieResponse 响应实体类
     */
    Flux<ErnieResponse> chatContOfStream(ChatErnieRequest chatErnieRequest, String msgUid);

    /**
     * ChatErnieRequest 参数校验
     *
     * @param request 请求实体类
     */
    void validChatErnieRequest(ChatErnieRequest request);
}
