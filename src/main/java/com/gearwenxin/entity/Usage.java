package com.gearwenxin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usage {

    /**
     * 问题tokens数
     */
    @JsonProperty("prompt_tokens")
    private int promptTokens;

    /**
     * 回答tokens数
     */
    @JsonProperty("completion_tokens")
    private int completionTokens;

    /**
     * tokens总数
     */
    @JsonProperty("total_tokens")
    private int totalTokens;

    /**
     * plugin消耗的tokens
     */
    @JsonProperty("plugins")
    private List<PluginUsage> plugins;

}
