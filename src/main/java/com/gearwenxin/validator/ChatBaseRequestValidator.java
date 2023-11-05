package com.gearwenxin.validator;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.exception.WenXinException;
import org.apache.commons.lang3.StringUtils;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;

public class ChatBaseRequestValidator implements RequestValidator<ChatBaseRequest> {
    @Override
    public void validate(ChatBaseRequest request) {
        // 检查content不为空
        if (StringUtils.isBlank(request.getContent())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
    }
}