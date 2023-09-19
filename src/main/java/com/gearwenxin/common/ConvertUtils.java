package com.gearwenxin.common;

import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.chatmodel.*;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.exception.BusinessException;

import java.util.Objects;

/**
 * 类型转换工具类
 *
 * @author Ge Mingjia
 * @date 2023/5/27
 */
public class ConvertUtils {

    public static ErnieRequest.ErnieRequestBuilder toErnieRequest(ChatErnieRequest chatRequest) {

        Objects.requireNonNull(chatRequest, "ChatErnieRequest is null");
        if (chatRequest.getContent() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is null!");
        }
        return ErnieRequest.builder()
                .userId(chatRequest.getUserId())
                .messages(WenXinUtils.buildUserMessageDeque(chatRequest.getContent()))
                .temperature(chatRequest.getTemperature())
                .topP(chatRequest.getTopP())
                .penaltyScore(chatRequest.getPenaltyScore());
    }

    public static BaseRequest.BaseRequestBuilder toBaseRequest(ChatBaseRequest chatRequest) {

        Objects.requireNonNull(chatRequest, "ChatBaseRequest is null");
        if (chatRequest.getContent() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is null!");
        }
        return BaseRequest.builder()
                .userId(chatRequest.getUserId())
                .messages(WenXinUtils.buildUserMessageDeque(chatRequest.getContent()));
    }

    public static PromptRequest toPromptRequest(ChatPromptRequest chatRequest) {

        Objects.requireNonNull(chatRequest, "ChatPromptRequest is null");
        if (chatRequest.getParamMap() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is null!");
        }
        return PromptRequest.builder()
                .id(String.valueOf(chatRequest.getId()))
                .paramMap(chatRequest.getParamMap())
                .build();
    }

}