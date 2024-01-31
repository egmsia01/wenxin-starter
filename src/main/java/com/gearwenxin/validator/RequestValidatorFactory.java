package com.gearwenxin.validator;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.exception.WenXinException;

public class RequestValidatorFactory {
    public static RequestValidator getValidator(ChatBaseRequest request) {
        if (request.getClass() == ChatBaseRequest.class) {
            return new ChatBaseRequestValidator();
        } else if (request.getClass() == ChatErnieRequest.class) {
            return new ChatErnieRequestValidator();
        } else {
            throw new WenXinException(ErrorCode.REQUEST_TYPE_ERROR);
        }
    }
}