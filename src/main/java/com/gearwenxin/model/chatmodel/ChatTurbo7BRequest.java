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
public class ChatTurbo7BRequest {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    private String userId;

    /**
     * 聊天信息,不能为空
     */
    private String content;

}
