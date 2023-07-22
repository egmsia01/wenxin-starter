package com.gearwenxin.common;

import com.gearwenxin.model.Message;
import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ErnieRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换工具类
 *
 * @author Ge Mingjia
 * @date 2023/5/27
 */
public class ConvertUtils {
    public static ErnieRequest chatErnieRequestToErnieRequest(ChatErnieRequest chatErnieRequest) {
        ErnieRequest ernieRequest = new ErnieRequest();

        if (chatErnieRequest.getUserId() != null) {
            ernieRequest.setUserId(chatErnieRequest.getUserId());
        }
        if (chatErnieRequest.getContent() != null) {
            List<Message> messageList = new ArrayList<>();
            messageList.add(new Message(RoleEnum.user, chatErnieRequest.getContent()));
            ernieRequest.setMessages(messageList);
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
//        if (chatErnieRequest.getStream() != null) {
//            ernieRequest.setStream(chatErnieRequest.getStream());
//        }

        return ernieRequest;
    }

}