package com.gearwenxin.entity.enums;

import lombok.Getter;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/10/15
 */
@Getter
public enum ChatType {
    once("_once"),
    cont("_continuous"),
    stream("_stream"),
    onceStream("_once_stream"),
    contStream("_continuous_stream");

    private final String value;

    ChatType(String value) {
        this.value = value;
    }

}

