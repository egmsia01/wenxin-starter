package com.gearwenxin.entity.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum ResponseFormatType {

    /**
     * 以json格式返回
     */
    json_object("json_object"),

    /**
     * 以文本格式返回
     */
    text("text");

    private final String value;

    ResponseFormatType(String value) {
        this.value = value;
    }

    public static ResponseFormatType TypeFromString(String text) {
        for (ResponseFormatType b : ResponseFormatType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static Optional<ResponseFormatType> fromString(String text) {
        return Optional.ofNullable(TypeFromString(text));
    }

    public static void ifPresent(String text, Runnable runnable) {
        fromString(text).ifPresent(result -> runnable.run());
    }
}
