package com.gearwenxin.entity.response.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.entity.Usage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回结果信息类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginResponse<T> {

    /**
     * 唯一的log id，用于问题定位
     */
    @JsonProperty("log_id")
    private String logId;

    /**
     * 本轮对话的id
     */
    @JsonProperty("id")
    private String id;

    /**
     * 回包类型。
     * chat.completion：多轮对话返回
     */
    @JsonProperty("object")
    private String object;

    /**
     * 时间戳
     */
    @JsonProperty("created")
    private int created;

    /**
     * 表示当前子句的序号，只有在流式接口模式下会返回该字段
     */
    @JsonProperty("sentence_id")
    private int sentenceId;

    /**
     * 表示当前子句是否是最后一句，只有在流式接口模式下会返回该字段
     */
    @JsonProperty("is_end")
    private boolean isEnd;

    /**
     * 插件返回结果
     */
    @JsonProperty("result")
    private String result;

    /**
     * 当前生成的结果是否被截断
     */
    @JsonProperty("is_truncated")
    private boolean isTruncated;

    /**
     * 表示用户输入是否存在安全，是否关闭当前会话，清理历史会话信息
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
     * false：否，表示用户输入无安全风险
     */
    @JsonProperty("need_clear_history")
    private boolean needClearHistory;

    /**
     * 当need_clear_history为true时，此字段会告知第几轮对话有敏感信息，如果是当前问题，ban_round = -1
     */
    @JsonProperty("ban_round")
    private int banRound;

    /**
     * token统计信息，token数 = 汉字数+单词数*1.3 （仅为估算逻辑）
     */
    @JsonProperty("usage")
    private Usage usage;

    /**
     * 插件的原始请求信息
     */
    @JsonProperty("meta_info")
    private T metaInfo;

}