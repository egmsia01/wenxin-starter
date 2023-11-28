package com.gearwenxin.entity.chatmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.entity.FunctionCall;
import com.gearwenxin.entity.FunctionInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Ge Mingjia
 * ContBot 模型
 */
@Slf4j
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

    /**
     * 一个可触发函数的描述列表
     */
    private List<FunctionInfo> functions;

    /**
     * 模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手，说明：
     * （1）长度限制1024个字符
     * （2）如果使用functions参数，不支持设定人设system
     */
    private String system;

    /**
     * message作者；当role=function时，必填，且是响应内容中function_call中的name
     */
    private String name;

    /**
     * 函数调用，function call场景下第一轮对话的返回，第二轮对话作为历史信息在message中传入
     */
    @JsonProperty("function_call")
    private FunctionCall functionCall;

}
