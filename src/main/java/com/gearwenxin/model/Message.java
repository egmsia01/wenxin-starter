package com.gearwenxin.model;

import com.gearwenxin.common.RoleEnum;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "role is null !")
    private RoleEnum role;

    /**
     * 对话内容，不能为空
     */
    @NotNull(message = "content is null !")
    private String content;
}
