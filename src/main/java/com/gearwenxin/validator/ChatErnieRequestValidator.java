package com.gearwenxin.validator;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.exception.WenXinException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.Constant.MAX_SYSTEM_LENGTH;

@Slf4j
public class ChatErnieRequestValidator implements RequestValidator {
    @Override
    public <T> void validate(T request) {
        ChatErnieRequest chatErnieRequest = (ChatErnieRequest) request;
        // 检查content不为空
        if (StringUtils.isBlank(chatErnieRequest.getContent())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (chatErnieRequest.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
        // 检查temperature和topP不都有值
        if (chatErnieRequest.getTemperature() != null && chatErnieRequest.getTopP() != null) {
            log.warn("Temperature and topP cannot both have value");
        }
        // 检查temperature范围
        if (chatErnieRequest.getTemperature() != null && (chatErnieRequest.getTemperature() <= 0 || chatErnieRequest.getTemperature() > 1.0)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "temperature should be in (0, 1]");
        }
        // 检查topP范围
        if (chatErnieRequest.getTopP() != null && (chatErnieRequest.getTopP() < 0 || chatErnieRequest.getTopP() > 1.0)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "topP should be in [0, 1]");
        }
        // 检查penaltyScore范围
        if (chatErnieRequest.getTemperature() != null && (chatErnieRequest.getPenaltyScore() < 1.0 || chatErnieRequest.getPenaltyScore() > 2.0)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "penaltyScore should be in [1, 2]");
        }
        // 检查system与function call
        if (StringUtils.isNotBlank(chatErnieRequest.getSystem()) && chatErnieRequest.getFunctions() != null) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "if 'function' not null, the 'system' must be null");
        }
        // 检查system长度
        if (chatErnieRequest.getSystem() != null && chatErnieRequest.getSystem().length() > MAX_SYSTEM_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "system's length cannot be more than 1024");
        }
    }
    
}