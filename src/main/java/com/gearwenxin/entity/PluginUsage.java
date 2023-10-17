package com.gearwenxin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * plugin_usage说明
 */
@Data
public class PluginUsage {

    /**
     * 插件名称，chatFile：chatfile插件消耗的tokens
     */
    @JsonProperty("name")
    private String name;

    /**
     * 解析文档tokens
     */
    @JsonProperty("parse_tokens")
    private int parseTokens;

    /**
     * 摘要文档tokens
     */
    @JsonProperty("abstract_tokens")
    private int abstractTokens;

    /**
     * 检索文档tokens
     */
    @JsonProperty("search_tokens")
    private int searchTokens;

    /**
     * 总tokens
     */
    @JsonProperty("total_tokens")
    private int totalTokens;

}
