package com.gearwenxin.entity;

/**
 * @author Ge Mingjia
 * @date 2023/10/3
 */
public class ClientParams<T> {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息UID
     */
    private String msgUid;

    /**
     * 参数类
     */
    private T params;

}
