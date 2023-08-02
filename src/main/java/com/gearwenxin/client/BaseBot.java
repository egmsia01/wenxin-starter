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
public interface BaseBot {

    /**
     * 单独设置accessToken
     *
     * @param accessToken accessToken
     */
    void setAccessToken(String accessToken);

    /**
     * 获取历史消息列表 MessageHistoryMap
     *
     * @return MessageHistoryMap
     */
    Map<String, Queue<Message>> getMessageHistoryMap();

    /**
     * 设置历史消息列表 MessageHistoryMap
     *
     * @param map 历史消息列表
     */
    void initMessageHistoryMap(Map<String, Queue<Message>> map);

    /**
     * 获取模型的URL
     *
     * @return URL
     */
    String getURL();

}
