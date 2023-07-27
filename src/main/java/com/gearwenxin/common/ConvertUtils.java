package com.gearwenxin.common;

import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.chatmodel.*;
import com.gearwenxin.model.request.ErnieRequest;
import com.gearwenxin.model.request.PromptRequest;
import com.gearwenxin.model.request.Turbo7BRequest;

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

    public static Turbo7BRequest chatTurboReq7BToTurboReq(ChatTurbo7BRequest chatTurbo7BRequest) {
        Turbo7BRequest turbo7BRequest = new Turbo7BRequest();

        if (chatTurbo7BRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (chatTurbo7BRequest.getUserId() != null) {
            turbo7BRequest.setUserId(chatTurbo7BRequest.getUserId());
        }
        if (chatTurbo7BRequest.getContent() != null) {
            Queue<Message> messageQueue = new LinkedList<>();
            messageQueue.add(new Message(RoleEnum.user, chatTurbo7BRequest.getContent()));
            turbo7BRequest.setMessages(messageQueue);
        }

        return turbo7BRequest;
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