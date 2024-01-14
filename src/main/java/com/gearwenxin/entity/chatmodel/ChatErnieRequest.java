package com.gearwenxin.entity.chatmodel;

import com.gearwenxin.entity.FunctionCall;
import com.gearwenxin.entity.FunctionInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 * <p>
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
    private FunctionCall functionCall;

    /**
     * 生成停止标识，当模型生成结果以stop中某个元素结尾时，停止文本生成。说明：
     * （1）每个元素长度不超过20字符
     * （2）最多4个元素
     */
    private List<String> stop;

    /**
     * 是否强制关闭实时搜索功能，默认false，表示不关闭
     */
    private Boolean disableSearch;

    /**
     * 是否开启上角标返回，说明：
     * （1）开启后，有概率触发搜索溯源信息search_info，search_info内容见响应参数介绍
     * （2）默认false，不开启
     */
    private Boolean enableCitation;

    /**
     * 指定响应内容的格式，说明：
     * （1）可选值：
     * · json_object：以json格式返回，可能出现不满足效果情况
     * · text：以文本格式返回
     * （2）如果不填写参数response_format值，默认为text
     */
    private String responseFormat;

}
