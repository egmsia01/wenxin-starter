package com.gearwenxin.service;

import com.gearwenxin.entity.response.ChatResponse;
import org.reactivestreams.Publisher;

@FunctionalInterface
public interface ChatRequestProcessor<R> {
    Publisher<ChatResponse> process(R request, String msgUid, boolean stream);
}
