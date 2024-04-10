package com.gearwenxin.config;

import com.gearwenxin.schedule.entity.ModelHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfig implements Serializable {

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
