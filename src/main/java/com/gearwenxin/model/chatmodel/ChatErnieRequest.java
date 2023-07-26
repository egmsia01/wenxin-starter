package com.gearwenxin.model.chatmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 * <p>
 * CommonBot 模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatErnieRequest {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    private String userId;

    /**
     * 聊天信息,不能为空
     */
    private String content;

    /**
     * 输出更加随机，而较低的数值会使其更加集中和确定，默认0.95，范围 (0, 1.0]
     */
    private Float temperature;

    /**
     * （影响输出文本的多样性，越大生成文本的多样性越强
     */
    private Float topP;

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象。说明：
     */
    private Float penaltyScore;

//    /**
//     * 是否以流式接口的形式返回数据，默认false
//     */
//    private Boolean stream;

}
