package com.gearwenxin.validator;

public interface RequestValidator<T> {
    void validate(T request);
}
