package com.gearwenxin.service;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import org.reactivestreams.Publisher;

public interface ChatProcess {

    /**
     * 单次聊天处理
     */
    <T extends ChatBaseRequest> Publisher<ChatResponse> chatSingleProcess(T requestT, boolean stream);

    /**
     * 连续聊天处理
     */
    <T extends ChatBaseRequest> Publisher<ChatResponse> chatContProcess(T requestT, String msgUid, boolean stream);

}
