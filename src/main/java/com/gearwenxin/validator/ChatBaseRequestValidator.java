package com.gearwenxin.validator;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.config.ModelConfig;
import org.apache.commons.lang3.StringUtils;

public class ChatBaseRequestValidator implements RequestValidator {

    @Override
    public <T> void validate(T request, ModelConfig config) {
        ChatBaseRequest chatBaseRequest = (ChatBaseRequest) request;
        // 检查content不为空
        if (StringUtils.isBlank(chatBaseRequest.getContent())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (chatBaseRequest.getContent().length() > config.getContentMaxLength()) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than " + config.getContentMaxLength());
        }
    }

}