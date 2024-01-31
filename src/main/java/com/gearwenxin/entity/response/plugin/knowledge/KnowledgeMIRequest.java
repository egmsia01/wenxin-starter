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
public class KnowledgeMIRequest {

    /**
     * 用于查询知识库的用户请求
     */
    @JsonProperty("query")
    private String query;

    /**
     * 使用知识库的 Id 列表
     */
    @JsonProperty("kbIds")
    private String[] kbIds;

    /**
     * 分片和query的相似度分数的下限，低于该下限的文档分片不会被返回
     */
    @JsonProperty("score")
    private float score;

    /**
     * 返回的最相关的文档数
     */
    @JsonProperty("topN")
    private Integer topN;

}
