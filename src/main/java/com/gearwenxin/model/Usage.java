package com.gearwenxin.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Data
public class Usage {

    /**
     * 问题tokens数
     */
    @SerializedName("prompt_tokens")
    private int promptTokens;

    /**
     * 回答tokens数
     */
    @SerializedName("completion_tokens")
    private int completionTokens;

    /**
     * tokens总数
     */
    @SerializedName("total_tokens")
    private int totalTokens;

}
