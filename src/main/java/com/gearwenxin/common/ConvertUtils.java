package com.gearwenxin.common;

import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.chatmodel.*;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.request.PromptRequest;

import java.util.Objects;

/**
 * 类型转换工具类
 *
 * @author Ge Mingjia
 * @date 2023/5/27
 */
public class ConvertUtils {

    public static ErnieRequest.ErnieRequestBuilder toErnieReq(ChatErnieRequest chatRequest) {

        Objects.requireNonNull(chatRequest, "ChatErnieRequest is null");

        return ErnieRequest.builder()
                .userId(chatRequest.getUserId())
                .messages(WenXinUtils.buildUserMessageQueue(chatRequest.getContent()))
                .temperature(chatRequest.getTemperature())
                .topP(chatRequest.getTopP())
                .penaltyScore(chatRequest.getPenaltyScore());
    }

    public static BaseRequest.BaseRequestBuilder convertToBaseRequest(ChatBaseRequest chatRequest) {

        Objects.requireNonNull(chatRequest, "ChatBaseRequest is null");

        return BaseRequest.builder()
                .userId(chatRequest.getUserId())
                .messages(WenXinUtils.buildUserMessageQueue(chatRequest.getContent()));
    }

    public static PromptRequest chatPromptReqToPromptReq(ChatPromptRequest chatRequest) {

        Objects.requireNonNull(chatRequest, "ChatPromptRequest is null");

        return PromptRequest.builder()
                .id(String.valueOf(chatRequest.getId()))
                .paramMap(chatRequest.getParamMap())
                .build();
    }

}