package com.gearwenxin.common;

import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.*;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.request.TurboRequest;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 类型转换工具类
 *
 * @author Ge Mingjia
 * @date 2023/5/27
 */
public class ConvertUtils {

    public static ErnieRequest chatErnieReqToErnieReq(ChatErnieRequest chatErnieRequest) {
        ErnieRequest ernieRequest = new ErnieRequest();

        if (chatErnieRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (chatErnieRequest.getUserId() != null) {
            ernieRequest.setUserId(chatErnieRequest.getUserId());
        }
        if (chatErnieRequest.getContent() != null) {
            Queue<Message> messageQueue = new LinkedList<>();
            messageQueue.add(new Message(RoleEnum.user, chatErnieRequest.getContent()));
            ernieRequest.setMessages(messageQueue);
        }
        if (chatErnieRequest.getTemperature() != null) {
            ernieRequest.setTemperature(chatErnieRequest.getTemperature());
        }
        if (chatErnieRequest.getTopP() != null) {
            ernieRequest.setTopP(chatErnieRequest.getTopP());
        }
        if (chatErnieRequest.getPenaltyScore() != null) {
            ernieRequest.setPenaltyScore(chatErnieRequest.getPenaltyScore());
        }

        return ernieRequest;
    }

    public static TurboRequest chatTurboReq7BToTurboReq(ChatTurboRequest chatTurboRequest) {
        TurboRequest turboRequest = new TurboRequest();

        if (chatTurboRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (chatTurboRequest.getUserId() != null) {
            turboRequest.setUserId(chatTurboRequest.getUserId());
        }
        if (chatTurboRequest.getContent() != null) {
            Queue<Message> messageQueue = new LinkedList<>();
            messageQueue.add(new Message(RoleEnum.user, chatTurboRequest.getContent()));
            turboRequest.setMessages(messageQueue);
        }

        return turboRequest;
    }

    public static PromptRequest chatPromptReqToPromptReq(ChatPromptRequest chatPromptRequest) {
        PromptRequest promptRequest = new PromptRequest();

        if (chatPromptRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (chatPromptRequest.getId() > 0) {
            promptRequest.setId(String.valueOf(chatPromptRequest.getId()));
        }
        if (chatPromptRequest.getParamMap() != null && !chatPromptRequest.getParamMap().isEmpty()) {
            promptRequest.setParamMap(chatPromptRequest.getParamMap());
        }
        return promptRequest;
    }


}