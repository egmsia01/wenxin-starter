package com.gearwenxin.client.basic;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.model.BasicChatModel;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.entity.ChatTask;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class BasicChatClient {

    private final ModelConfig modelConfig;

    private static final float defaultWeight = 0;

    public BasicChatClient(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    private static final TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    public Mono<ChatResponse> chat(String content) {
        return chat(content, defaultWeight);
    }

    public Mono<ChatResponse> chat(String content, float weight) {
        ChatErnieRequest request = new ChatErnieRequest();
        request.setContent(content);
        return chat(request, weight);
    }

    public <T extends ChatBaseRequest> Mono<ChatResponse> chat(T chatRequest) {
        return chat(chatRequest, defaultWeight);
    }

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

    public Flux<ChatResponse> chatStream(String content) {
        return chatStream(content, defaultWeight);
    }

    public Flux<ChatResponse> chatStream(String content, float weight) {
        ChatErnieRequest request = new ChatErnieRequest();
        request.setContent(content);
        return chatStream(request, weight);
    }

    public <T extends ChatBaseRequest> Flux<ChatResponse> chatStream(T chatRequest) {
        return chatStream(chatRequest, defaultWeight);
    }

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

}
