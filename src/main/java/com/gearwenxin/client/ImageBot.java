package com.gearwenxin.client;

import com.gearwenxin.model.chatmodel.ChatPromptRequest;
import com.gearwenxin.model.response.PromptResponse;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ImageBot {

    /**
     * 绘图
     *
     * @param
     * @return ChatResponse 响应实体类
     */
    String chatImage(String content, int width, int height);

}
