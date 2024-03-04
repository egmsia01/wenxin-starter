package com.gearwenxin.client;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.model.ChatModel;
import com.gearwenxin.schedule.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ChatClient implements ChatModel {

    private final String modelName;
    private static final float defaultWeight = 0;

    public ChatClient(String modelName) {
        this.modelName = modelName;
    }

    TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    @Override
    public Mono<ChatResponse> chat(String content) {
        return null;
    }

    @Override
    public Mono<ChatResponse> chat(String content, float weight) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest, float weight) {
        return null;
    }

    @Override
    public Flux<ChatResponse> chatStream(String content) {
        ChatBaseRequest request = ChatBaseRequest.builder().content(content).build();
        ChatTask chatTask = ChatTask.builder()
                .modelName(modelName)
                .taskType(ModelType.chat)
                .taskRequest(request)
                .taskWeight(defaultWeight)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        CompletableFuture<Flux<ChatResponse>> completableFuture = taskQueueManager.getChatFutureMap().get(taskId);
        completableFuture.thenAccept(flux -> flux.subscribe(System.out::println));
        return Flux.create(sink -> {
            log.info("Flux create");
            completableFuture.thenAcceptAsync(flux -> {
                log.info("CompletableFuture thenAccept");
                flux.subscribe(sink::next);
            });
        });
    }

    @Override
    public Flux<ChatResponse> chatStream(String content, float weight) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest, float weight) {
        return null;
    }

    @Override
    public Mono<ChatResponse> chats(String content, String msgUid) {
        return null;
    }

    @Override
    public Mono<ChatResponse> chats(String content, String msgUid, float weight) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid, float weight) {
        return null;
    }

    @Override
    public Flux<ChatResponse> chatsStream(String content, String msgUid) {
        return null;
    }

    @Override
    public Flux<ChatResponse> chatsStream(String content, String msgUid, float weight) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T chatRequest, String msgUid) {
        return null;
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T chatRequest, String msgUid, float weight) {
        return null;
    }
}
