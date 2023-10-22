package com.gearwenxin.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 请求参数类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginParams {

    /**
     * 查询信息
     * （1）成员不能为空
     * （2）长度不能超过1000个字符
     */
    @JsonProperty("query")
    private String query;

    /**
     * 需要调用的插件，参数为插件ID，插件ID可在插件列表-插件详情中获取。
     * （1）最多3个插件，最少1个插件。
     * （2）当多个插件时，插件触发由大模型意图判断控制。
     * （3）当只有1个插件时，强制指定使用该插件工具。
     */
    @JsonProperty("plugins")
    private List<String> plugins;

    /**
     * 是否以流式接口的形式返回数据，默认false，可选值如下：
     * （1）true: 是，以流式接口的形式返回数据
     * （2）false：否，非流式接口形式返回数据
     */
    @JsonProperty("stream")
    private boolean stream;

    /**
     * llm相关参数，不指定参数时，使用调试过程中的默认值。
     */
    @JsonProperty("llm")
    private Object llm;

    /**
     * 如果prompt中使用了变量，推理时可以填写具体值；
     * 如果prompt中未使用变量，该字段不填。
     */
    @JsonProperty("input_variables")
    private Object inputVariables;

    /**
     * 聊天上下文信息。
     */
    @JsonProperty("history")
    private Object history;

    /**
     * 是否返回插件的原始请求信息，默认false，可选值如下：
     * true：是，返回插件的原始请求信息meta_info
     * false：否，不返回插件的原始请求信息meta_info
     */
    @JsonProperty("verbose")
    private boolean verbose;

    /**
     * 文件的http地址
     */
    @JsonProperty("fileurl")
    private String fileUrl;

}
