package com.gearwenxin.service.impl;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.service.ChatProcess;
import org.reactivestreams.Publisher;

public class ChatProcessImpl implements ChatProcess {
    @Override
    public <T extends ChatBaseRequest> Publisher<ChatResponse> chatSingleProcess(T requestT, boolean stream) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Publisher<ChatResponse> chatContProcess(T requestT, String msgUid, boolean stream) {
        return null;
    }
}
