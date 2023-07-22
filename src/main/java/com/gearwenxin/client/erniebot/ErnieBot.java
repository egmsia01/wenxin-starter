package com.gearwenxin.client.erniebot;

import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ErnieResponse;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ErnieBot {

    /**
     * 单轮对话，无上下文记忆，不支持参数配置
     *
     * @param content 对话内容
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatSingleRound(String content);

    /**
     * 单轮对话，无上下文记忆，支持参数配置
     *
     * @param chatErnieRequest 请求实体类
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatSingleRound(ChatErnieRequest chatErnieRequest);

    /**
     * 多轮对话，有上下文记忆，不支持参数配置
     *
     * @param content 对啊胡内容
     * @param msgUID  对话唯一识别码
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatMultipleRounds(String content, String msgUID);

    /**
     * 多轮对话，有上下文记忆，支持参数配置
     *
     * @param chatErnieRequest 请求实体类
     * @param msgUID  对话唯一识别码
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatMultipleRounds(ChatErnieRequest chatErnieRequest, String msgUID);

    /**
     * ChatErnieRequest 参数校验
     *
     * @param request 请求实体类
     */
    void validChatErnieRequest(ChatErnieRequest request);
}
