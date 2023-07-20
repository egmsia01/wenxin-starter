package com.gearwenxin.model.erniebot;

import com.gearwenxin.annotations.Between;
import com.gearwenxin.annotations.Only;
import com.gearwenxin.model.Message;
import com.google.gson.annotations.SerializedName;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 * <p>
 * ErnieBot 模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErnieRequest {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    @SerializedName("user_id")
    private String userId;

    /**
     * 聊天上下文信息.
     * （1）messages成员不能为空，1个成员表示单轮对话，多个成员表示多轮对话
     * （2）最后一个message为当前请求的信息，前面的message为历史对话信息
     * （3）必须为奇数个成员，成员中message的role必须依次为user、assistant
     * （4）最后一个message的content长度（即此轮对话的问题）不能超过2000个字符；
     * 如果messages中content总长度大于2000字符，系统会依次遗忘最早的历史会话，直到content的总长度不超过2000个字符
     */
    @Size(max = 2000)
    @SerializedName("messages")
    @NotNull(message = "messages is null !")
    private List<Message> messages;

    /**
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认0.95，范围 (0, 1.0]，不能为0
     * （3）建议该参数和top_p只设置1个
     * （4）建议top_p和temperature不要同时更改
     */
    @Only(value = 0)
    @Between(min = 0, max = 1.0, includeMax = true)
    @SerializedName("temperature")
    private Float temperature;

    /**
     * （1）影响输出文本的多样性，取值越大，生成文本的多样性越强
     * （2）默认0.8，取值范围 [0, 1.0]
     * （3）建议该参数和temperature只设置1个
     * （4）建议top_p和temperature不要同时更改
     */
    @Only(value = 0)
    @Between(min = 0, max = 1.0, includeMin = true, includeMax = true)
    @SerializedName("top_p")
    private Float topP;

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象。说明：
     * （1）值越大表示惩罚越大
     * （2）默认1.0，取值范围：[1.0, 2.0]
     */
    @Between(min = 1.0, max = 2.0, includeMin = true, includeMax = true)
    @SerializedName("penalty_score")
    private Float penaltyScore;

    /**
     * 是否以流式接口的形式返回数据，默认false
     */
    @SerializedName("stream")
    private Boolean stream;

}
