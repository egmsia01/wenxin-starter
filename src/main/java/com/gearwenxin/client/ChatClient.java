package com.gearwenxin.client;

import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.model.ChatModel;
import com.gearwenxin.schedule.entity.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ChatClient implements ChatModel {

    private final ModelConfig modelConfig;

    private static final float defaultWeight = 0;

    public ChatClient(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    private static final TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    @Override
    public Mono<ChatResponse> chat(String content) {
        return chat(content, defaultWeight);
    }

    @Override
    public Mono<ChatResponse> chat(String content, float weight) {
        ChatErnieRequest request = new ChatErnieRequest();
        request.setContent(content);
        return chat(request, weight);
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest) {
        return chat(chatRequest, defaultWeight);
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest, float weight) {
        ChatTask chatTask = ChatTask.builder()
                .modelConfig(modelConfig)
                .taskType(ModelType.chat)
                .taskRequest(chatRequest)
                .taskWeight(weight)
                .stream(false)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        return Mono.from(taskQueueManager.getChatFuture(taskId).join());
    }

    @Override
    public Flux<ChatResponse> chatStream(String content) {
        return chatStream(content, defaultWeight);
    }

    @Override
    public Flux<ChatResponse> chatStream(String content, float weight) {
        ChatErnieRequest request = new ChatErnieRequest();
        request.setContent(content);
        return chatStream(request, weight);
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest) {
        return chatStream(chatRequest, defaultWeight);
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T request, float weight) {
        ChatTask chatTask = ChatTask.builder()
                .modelConfig(modelConfig)
                .taskType(ModelType.chat)
                .taskRequest(request)
                .taskWeight(weight)
                .stream(true)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        return Flux.from(taskQueueManager.getChatFuture(taskId).join());
    }

    @Override
    public Mono<ChatResponse> chats(String content, String msgUid) {
        return chats(content, msgUid, defaultWeight);
    }

    @Override
    public Mono<ChatResponse> chats(String content, String msgUid, float weight) {
        return chatsStream(content, msgUid, weight).next();
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid) {
        return chats(chatRequest, msgUid, defaultWeight);
    }

    @Override
    public <T extends ChatBaseRequest> Mono<ChatResponse> chats(T chatRequest, String msgUid, float weight) {
        return chatsStream(chatRequest, msgUid, weight).next();
    }

    @Override
    public Flux<ChatResponse> chatsStream(String content, String msgUid) {
        return chatsStream(content, msgUid, defaultWeight);
    }

    @Override
    public Flux<ChatResponse> chatsStream(String content, String msgUid, float weight) {
        ChatErnieRequest request = new ChatErnieRequest();
        request.setContent(content);
        return chatsStream(request, msgUid, weight);
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T chatRequest, String msgUid) {
        return chatsStream(chatRequest, msgUid, defaultWeight);
    }

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatsStream(T request, String msgUid, float weight) {
        ChatTask chatTask = ChatTask.builder()
                .modelConfig(modelConfig)
                .taskType(ModelType.chat)
                .taskRequest(request)
                .messageId(msgUid)
                .taskWeight(weight)
                .stream(true)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        return Flux.from(taskQueueManager.getChatFuture(taskId).join());
    }

}
