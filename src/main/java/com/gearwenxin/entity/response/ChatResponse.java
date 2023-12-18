package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.entity.FunctionCall;
import com.gearwenxin.entity.Usage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 * <p>
 * ContBot 模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse implements Serializable {

    /**
     * 本轮对话的id
     */
    @JsonProperty("id")
    private String id;

    /**
     * 用于定位的log_id
     */
    @JsonProperty("log_id")
    private String logId;

    /**
     * 回包类型
     * chat.completion：多轮对话返回
     */
    @JsonProperty("object")
    private String object;

    /**
     * 时间戳
     */
    @JsonProperty("created")
    private Integer created;

    /**
     * 表示当前子句的序号。只有在流式接口模式下会返回该字段
     */
    @JsonProperty("sentence_id")
    private Integer sentenceId;

    /**
     * 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段
     */
    @JsonProperty("is_end")
    private Boolean isEnd;

    /**
     * 当前生成的结果是否被截断
     */
    @JsonProperty("is_truncated")
    private Boolean isTruncated;

    /**
     * 输出内容标识，说明：
     * · normal：输出内容完全由大模型生成，未触发截断、替换
     * · stop：输出结果命中入参stop中指定的字段后被截断
     * · length：达到了最大的token数，根据EB返回结果is_truncated来截断
     * · content_filter：输出内容被截断、兜底、替换为**等
     * · function_call：调用了funtion call功能
     */
    @JsonProperty("finish_reason")
    private String finishReason;

    /**
     * 搜索数据，当请求参数enable_citation为true并且触发搜索时，会返回该字段
     */
    @JsonProperty("search_info")
    private SearchInfo searchInfo;

    /**
     * 对话返回结果
     */
    @JsonProperty("result")
    private String result;

    /**
     * 表示用户输入是否存在安全，是否关闭当前会话，清理历史回话信息
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息
     * false：否，表示用户输入无安全风险
     */
    @JsonProperty("need_clear_history")
    private Boolean needClearHistory;

    /**
     * token统计信息，token数 = 汉字数+单词数*1.3 （仅为估算逻辑）
     */
    @JsonProperty("usage")
    private Usage usage;

    /**
     * 当need_clear_history为true时，此字段会告知第几轮对话有敏感信息，如果是当前问题，ban_round=-1
     */
    @JsonProperty("ban_round")
    private Integer banRound;

    /**
     * 说明：
     * · 0：正常返回
     * · 其他：非正常
     */
    @JsonProperty("flag")
    private Integer flag;

    /**
     * 错误代码，正常为 null
     */
    @JsonProperty("error_code")
    private Integer errorCode;

    /**
     * 错误代码，正常为 null
     */
    @JsonProperty("eb_code")
    private Integer ebCode;

    /**
     * 错误信息，正常为 null
     */
    @JsonProperty("error_msg")
    private String errorMsg;

    /**
     * 由模型生成的函数调用，包含函数名称，和调用参数
     */
    @JsonProperty("function_call")
    private FunctionCall functionCall;

}
