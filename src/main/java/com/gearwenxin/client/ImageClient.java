package com.gearwenxin.client;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.model.ImageModel;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.entity.ChatTask;
import reactor.core.publisher.Mono;

public class ImageClient implements ImageModel {

    private final ModelConfig modelConfig;

    private static final float defaultWeight = 0;

    TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    public ImageClient(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    @Override
    public Mono<ImageResponse> chatImage(ImageBaseRequest imageBaseRequest) {
        return chatImage(imageBaseRequest, defaultWeight);
    }

    @Override
    public Mono<ImageResponse> chatImage(ImageBaseRequest imageBaseRequest, float weight) {
        ChatTask chatTask = ChatTask.builder()
                .modelConfig(modelConfig)
                .taskType(ModelType.image)
                .taskRequest(imageBaseRequest)
                .taskWeight(weight)
                .build();
        String taskId = taskQueueManager.addTask(chatTask);
        return Mono.from(taskQueueManager.getImageFuture(taskId).join());
    }
}
