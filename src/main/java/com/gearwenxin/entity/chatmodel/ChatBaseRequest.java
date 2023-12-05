package com.gearwenxin.entity.chatmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/8/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatBaseRequest implements Serializable {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    private String userId;

    /**
     * 聊天信息,不能为空
     */
    private String content;

}
