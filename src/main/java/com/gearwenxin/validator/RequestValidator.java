package com.gearwenxin.validator;

import com.gearwenxin.config.ModelConfig;public interface RequestValidator {
    <T> void validate(T request, ModelConfig config);
}
