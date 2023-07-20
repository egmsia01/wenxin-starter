package com.gearwenxin.client.erniebot;

import com.gearwenxin.model.erniebot.ErnieResponse;
import com.gearwenxin.model.erniebot.ErnieRequest;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ErnieBot {

    /**
     * 单轮对话，无上下文记忆，支持参数配置
     *
     * @param content 对话内容
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatWithSingleRound(String content);

    /**
     * 单轮对话，无上下文记忆，支持参数配置
     *
     * @param ernieRequest 请求实体类
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatWithSingleRound(ErnieRequest ernieRequest);

    /**
     * 单轮对话，无上下文记忆
     *
     * @param ernieRequest 请求实体类
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatMultipleRounds(ErnieRequest ernieRequest);

    /**
     * 单轮对话，无上下文记忆
     *
     * @param content 请求实体类
     * @return ErnieResponse 响应实体类
     */
    ErnieResponse chatMultipleRounds(String content);
}
