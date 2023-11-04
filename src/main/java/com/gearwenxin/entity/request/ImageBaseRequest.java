package com.gearwenxin.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.enums.SamplerType;
import com.gearwenxin.exception.WenXinException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ge Mingjia
 * @date 2023/8/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageBaseRequest {

    /**
     * 提示词，即用户希望图片包含的元素。长度限制为1024字符，建议中文或者英文单词总数量不超过150个
     */
    @JsonProperty("prompt")
    private String prompt;

    /**
     * 反向提示词，即用户希望图片不包含的元素。长度限制为1024字符，建议中文或者英文单词总数量不超过150个
     */
    @JsonProperty("negative_prompt")
    private String negativePrompt;

    /**
     * 生成图片长宽，默认值 1024x1024，取值范围如下：
     * ["512x512", "768x768", "768x1024", "1024x768", "576x1024", "1024x576", "1024x1024"]
     * 注意：建议选择较大尺寸，结合完善的prompt，以保障图片质量。
     */
    @JsonProperty("size")
    private String size = "1024x1024";

    /**
     * 生成图片数量，说明：
     * · 默认值为1
     * · 取值范围为1-4
     * · 单次生成的图片较多及请求较频繁可能导致请求超时
     */
    @JsonProperty("n")
    private Integer n = 1;

    /**
     * 迭代轮次，说明：
     * · 默认值为20
     * · 取值范围为10-50
     */
    @JsonProperty("steps")
    private Integer steps = 20;

    @JsonProperty("sampler_index")
    private String samplerIndex = SamplerType.DPM2_a.getValue();

    @JsonProperty("user_id")
    private String userId;

    public void validSelf() {

        // 检查content不为空
        if (StringUtils.isBlank(prompt)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "prompt cannot be empty");
        }
        // 检查单个content长度
        if (prompt.length() > 1024) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "prompt's length cannot be more than 1024");
        }
        // 检查单个content长度
        if (negativePrompt.length() > 1024) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "prompt's length cannot be more than 1024");
        }

    }

}
