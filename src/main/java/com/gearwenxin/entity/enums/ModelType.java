package com.gearwenxin.entity.enums;

import lombok.Getter;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/10/15
 */
@Getter
public enum ModelType {
    chat("chat"),
    prompt("prompt"),
    image("image"),
    embedding("embedding"),
    addTask("addTask"),
    check("check"),;

    private final String value;

    ModelType(String value) {
        this.value = value;
    }

}

