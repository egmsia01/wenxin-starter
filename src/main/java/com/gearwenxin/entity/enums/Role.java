package com.gearwenxin.entity.enums;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public enum Role {

    /**
     * 用户
     */
    user,

    /**
     * AI回复
     */
    assistant,

    /**
     * 只存在于function call的examples中
     */
    function

}
