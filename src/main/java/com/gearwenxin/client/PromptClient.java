package com.gearwenxin.client;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.entity.response.PromptResponse;
import com.gearwenxin.model.PromptModel;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.entity.ChatTask;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PromptClient implements PromptModel {

    private final ModelConfig modelConfig;

    private static final float defaultWeight = 0;

    TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    public PromptClient(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    @Override
    public Mono<PromptResponse> chat(ChatPromptRequest chatRequest) {
        return chat(chatRequest, defaultWeight);
    }

    @Override
    public Mono<PromptResponse> chat(ChatPromptRequest chatRequest, float weight) {
        ChatTask chatTask = ChatTask.builder()
                .modelConfig(modelConfig)
                .taskType(ModelType.prompt)
                .taskRequest(chatRequest)
                .taskWeight(weight)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        return Mono.from(taskQueueManager.getPromptFuture(taskId).join());
    }

}
