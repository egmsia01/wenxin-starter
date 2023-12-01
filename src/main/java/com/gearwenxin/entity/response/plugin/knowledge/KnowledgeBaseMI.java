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
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeBaseMI {

    /**
     * 插件 Id，为“uuid-zhishiku”
     */
    @JsonProperty("plugin_id")
    private String pluginId;

    /**
     * 知识库原始请求参数
     */
    @JsonProperty("request")
    private KnowledgeMIRequest request;

    /**
     * 知识库原始返回结果
     */
    @JsonProperty("response")
    private KnowledgeMIResponse response;

}
