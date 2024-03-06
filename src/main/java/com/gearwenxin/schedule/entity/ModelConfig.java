package com.gearwenxin.schedule.entity;

import lombok.Data;

@Data
public class ModelConfig {

    private String taskId;

    private String modelName;

    private String modelUrl;

    private String accessToken;

    private Integer contentMaxLength;

}
