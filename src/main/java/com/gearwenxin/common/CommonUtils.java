package com.gearwenxin.common;

import com.gearwenxin.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ge Mingjia
 * @date 2023/7/23
 */
public class CommonUtils {

    public static List<Message> buildMessageList(String content) {
        List<Message> messageList = new ArrayList<>();
        messageList.add(buildUserMessage(content));
        return messageList;
    }

    public static Message buildUserMessage(String content) {
        return new Message(RoleEnum.user, content);
    }

    public static Message buildAssistantMessage(String content) {
        return new Message(RoleEnum.assistant, content);
    }

}
