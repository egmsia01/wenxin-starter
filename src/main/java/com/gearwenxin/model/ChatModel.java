package com.gearwenxin.model;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatModel {

    /** 单次对话 **/
    Mono<ChatResponse> chat(String content);

    Mono<ChatResponse> chat(String content, float weight);

    <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest);

    <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest, float weight);

    Flux<ChatResponse> chatStream(String content);

    Flux<ChatResponse> chatStream(String content, float weight);

    <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest);

    <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest, float weight);

    /** 连续对话 **/
    Mono<ChatResponse> chats(String content, String msgUid);

    Mono<ChatResponse> chats(String content, String msgUid, float weight);

    <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid);

    <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid, float weight);

    Flux<ChatResponse> chatsStream(String content, String msgUid);

    Flux<ChatResponse> chatsStream(String content, String msgUid, float weight);

    <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T chatRequest, String msgUid);

    <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T chatRequest, String msgUid, float weight);

}
