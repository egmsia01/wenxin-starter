package com.gearwenxin.validator;

import com.gearwenxin.config.ModelConfig;

public class RequestValidatorFactory {

    public static RequestValidator getValidator(ModelConfig config) {

        if (config.getModelName().toLowerCase().contains("ernie")) {
            return new ChatErnieRequestValidator();
        } else {
            return new ChatBaseRequestValidator();
        }
    }
}