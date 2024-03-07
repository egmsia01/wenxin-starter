package com.gearwenxin.schedule.entity;

import lombok.Data;

@Data
public class ModelConfig {

    /**
     * 任务id, 无需传，SDK内部使用
     */
    private String taskId;

    private String modelName;

    private String modelUrl;

    private String accessToken;

    private Integer contentMaxLength = 8000;

    private ModelHeader modelHeader;

    private boolean enableStringResponse;

}
