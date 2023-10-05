package com.gearwenxin.entity;

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
    private RoleEnum role;

    /**
     * 对话内容，不能为空
     */
    private String content;
}
