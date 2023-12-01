package com.gearwenxin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/10/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private T paramObj;

}
