package com.gearwenxin.common;

import com.gearwenxin.model.Message;
import com.gearwenxin.model.erniebot.*;

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


}