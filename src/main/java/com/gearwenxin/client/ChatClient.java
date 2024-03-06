package com.gearwenxin.client;

import com.gearwenxin.common.ModelConfig;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.model.ChatModel;
import com.gearwenxin.schedule.BlockingMap;
import com.gearwenxin.schedule.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ChatClient implements ChatModel {

    private final String modelName;
    private final String modelUrl;

    private ModelConfig modelConfig;

    private static final float defaultWeight = 0;

    public ChatClient(String modelName, String modelUrl) {
        this.modelName = modelName;
        this.modelUrl = modelUrl;
    }

    public ChatClient(String modelName) {
        this.modelName = modelName;
        this.modelUrl = null;
    }

    public ChatClient(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
        this.modelName = modelConfig.getModelName();
        this.modelUrl = modelConfig.getModelUrl();
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
        ChatErnieRequest request = new ChatErnieRequest();
        request.setContent(content);
        ChatTask chatTask = ChatTask.builder()
                .modelName(modelName)
                .taskType(ModelType.chat)
                .taskRequest(request)
                .taskWeight(defaultWeight)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        BlockingMap<String, CompletableFuture<Flux<ChatResponse>>> chatFutureMap = taskQueueManager.getChatFutureMap();
        return chatFutureMap.get(taskId).join();
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
