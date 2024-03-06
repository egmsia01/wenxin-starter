package com.gearwenxin.schedule;

import com.gearwenxin.common.ModelConfig;
import com.gearwenxin.entity.enums.ModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatTask {

    private String taskId;

    private ModelConfig modelConfig;

    private ModelType taskType;

    private Object taskRequest;

    private Float taskWeight;

    private String messageId;

}
