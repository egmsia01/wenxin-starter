package com.gearwenxin.entity.chatmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 * <p>
 * ContBot 模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatErnieRequest extends ChatBaseRequest {

    /**
     * 输出更加随机，而较低的数值会使其更加集中和确定，默认0.95，范围 (0, 1.0]
     */
    private Float temperature;

    /**
     * （影响输出文本的多样性，越大生成文本的多样性越强
     */
    private Float topP;

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象。
     */
    private Float penaltyScore;

    // TODO: 增加函数触发列表 List<FunctionInfo> functions;

    /**
     * 模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手，说明：
     * （1）长度限制1024个字符
     * （2）如果使用functions参数，不支持设定人设system
     */
    // TODO: 如果使用functions参数，不支持设定人设 system
    private String system;

}
