package com.gearwenxin.entity.response.plugin.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia

 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeMIResponse {

    /**
     * 错误码
     */
    @JsonProperty("retCode")
    private Integer retCode;

    /**
     * 错误信息
     */
    @JsonProperty("message")
    private String message;

    /**
     * 返回结果
     */
    @JsonProperty("result")
    private KnowledgeMIResult result;

}
