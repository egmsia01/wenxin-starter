package com.gearwenxin.schedule;

import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
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

    private String modelName;

    private ModelType taskType;

    private ChatBaseRequest taskRequest;

    private Float taskWeight;

}
