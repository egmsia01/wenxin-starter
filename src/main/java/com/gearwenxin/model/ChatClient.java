package com.gearwenxin.model;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatClient {

    /** 单次对话 **/
    Mono<ChatResponse> chat(String content);

    <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest);

    Flux<ChatResponse> chatStream(String content);

    <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest);

    /** 连续对话 **/
    Mono<ChatResponse> chats(String content, String msgUid);

    <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid);

    Flux<ChatResponse> chatsStream(String content, String msgUid);

    <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T chatRequest, String msgUid);

}
