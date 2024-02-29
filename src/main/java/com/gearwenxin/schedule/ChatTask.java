package com.gearwenxin.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatTask {

    private String modelName;

    private Object taskRequest;

    private Float taskWeight;

}
