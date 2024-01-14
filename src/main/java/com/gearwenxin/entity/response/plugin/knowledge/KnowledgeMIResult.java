package com.gearwenxin.entity.response.plugin.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/10/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeMIResult {

    /**
     * bes查询耗时
     */
    @JsonProperty("besQueryCostMilsec3")
    private Integer besQueryCostMilsec3;

    /**
     * db查询耗时
     */
    @JsonProperty("dbQueryCostMilsec1")
    private Integer dbQueryCostMilsec1;

    /**
     * embedding查询耗时
     */
    @JsonProperty("embeddedCostMilsec2")
    private Integer embeddedCostMilsec2;

    /**
     * 知识库返回的最相关文档信息
     */
    @JsonProperty("responses")
    private KnowledgeMIResponses responses;

    /**
     * bos url生成耗时
     */
    @JsonProperty("urlSignedCostMilsec4")
    private Integer urlSignedCostMilsec4;

}
