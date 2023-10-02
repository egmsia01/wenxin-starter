package com.gearwenxin.service;

import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.model.ContBot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/9/27
 */
public class ContService implements ContBot {

    @Override
    public Mono<ChatResponse> chatCont(String content, String msgUid) {
        return null;
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return null;
    }

    @Override
    public Mono<ChatResponse> chatCont(Object chatRequest, String msgUid) {
        return null;
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(Object chatRequest, String msgUid) {
        return null;
    }
}