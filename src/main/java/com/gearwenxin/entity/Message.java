package com.gearwenxin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    /**
     * 当前支持以下：
     * user: 表示用户
     * assistant: 表示对话助手
     */
    private Role role;

    /**
     * 对话内容，不能为空
     */
    private String content;

    /**
     * message作者；当role=function时，必填，且是响应内容中function_call中的name
     */
    private String name;

    /**
     * 函数调用，function call场景下第一轮对话的返回，第二轮对话作为历史信息在message中传入
     */
    @JsonProperty("function_call")
    private FunctionCall functionCall;

}
