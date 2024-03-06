package com.gearwenxin.common;

import lombok.Data;

@Data
public class ModelConfig {

    private String modelName;

    private String modelUrl;

    private String accessToken;

    private Integer contentMaxLength;

}
