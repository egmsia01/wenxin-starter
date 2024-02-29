package com.gearwenxin.schedule;

import lombok.Data;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Data
public class ChatTask {

    private String modelName;

    private Object taskRequest;

    private Float taskWeight;

}
