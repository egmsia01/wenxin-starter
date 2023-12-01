package com.gearwenxin.entity.chatmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.FunctionCall;
import com.gearwenxin.entity.FunctionInfo;
import com.gearwenxin.exception.WenXinException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.Constant.MAX_SYSTEM_LENGTH;

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
    @JsonProperty("function_call")
    private FunctionCall functionCall;

//    public void validSelf() {
//
//        // 检查content不为空
//        if (StringUtils.isBlank(getContent())) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
//        }
//        // 检查单个content长度
//        if (getContent().length() > MAX_CONTENT_LENGTH) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
//        }
//        // 检查temperature和topP不都有值
//        if (getTemperature() != null && this.getTopP() != null) {
//            log.warn("Temperature and topP cannot both have value");
//        }
//        // 检查temperature范围
//        if (getTemperature() != null && (getTemperature() <= 0 || getTemperature() > 1.0)) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "temperature should be in (0, 1]");
//        }
//        // 检查topP范围
//        if (getTopP() != null && (getTopP() < 0 || getTopP() > 1.0)) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "topP should be in [0, 1]");
//        }
//        // 检查penaltyScore范围
//        if (getTemperature() != null && (getPenaltyScore() < 1.0 || getPenaltyScore() > 2.0)) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "penaltyScore should be in [1, 2]");
//        }
//        // 检查system与function call
//        if (StringUtils.isBlank(getSystem()) && getFunctions() != null) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "if 'function' not null, the 'system' must be null");
//        }
//        // 检查system长度
//        if (getSystem().length() > MAX_SYSTEM_LENGTH) {
//            throw new WenXinException(ErrorCode.PARAMS_ERROR, "system's length cannot be more than 1024");
//        }
//
//    }
}
