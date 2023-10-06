package com.gearwenxin.service;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.model.completions.CompletionsBot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/10/6
 */
public class CompletionsService implements CompletionsBot {
    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        return null;
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chatSingle(T chatRequest) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatSingleOfStream(T chatRequest) {
        return null;
    }
}
