package com.gearwenxin.validator;

import com.gearwenxin.schedule.entity.ModelConfig;public interface RequestValidator {
    <T> void validate(T request, ModelConfig config);
}
