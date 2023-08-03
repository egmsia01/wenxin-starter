package com.gearwenxin.entity.chatmodel;

/**
 * @author Ge Mingjia
 * @date 2023/8/3
 */
public class ChatBaseRequest {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    private String userId;

    /**
     * 聊天信息,不能为空
     */
    private String content;

}
