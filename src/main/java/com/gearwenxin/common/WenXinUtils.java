package com.gearwenxin.common;

import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.entity.Message;

import java.util.LinkedList;
import java.util.Queue;

import static com.gearwenxin.common.Constant.MAX_TOTAL_LENGTH;

/**
 * @author Ge Mingjia
 * @date 2023/7/23
 */
public class WenXinUtils {

    public static Queue<Message> buildUserMessageQueue(String content) {
        Queue<Message> messageQueue = new LinkedList<>();
        Message message = buildUserMessage(content);
        messageQueue.offer(message);
        return messageQueue;
    }

    public static Queue<Message> buildMessageQueue(Message userMessage, Message assistantMessage) {
        Queue<Message> messageQueue = new LinkedList<>();
        offerMessage(messageQueue, userMessage);
        offerMessage(messageQueue, assistantMessage);
        return messageQueue;
    }

    public static Message buildUserMessage(String content) {
        return new Message(RoleEnum.user, content);
    }

    public static Message buildAssistantMessage(String content) {
        return new Message(RoleEnum.assistant, content);
    }

    /**
     * 向历史消息中添加消息
     *
     * @param messagesHistory 历史消息队列
     * @param message         需添加的Message
     */
    public static void offerMessage(Queue<Message> messagesHistory, Message message) {

        if (messagesHistory == null || message == null || message.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        messagesHistory.offer(message);

        if (message.getRole() == RoleEnum.assistant) {
            return;
        }

        int totalLength = 0;
        for (Message msg : messagesHistory) {
            if (msg.getRole() == RoleEnum.user) {
                totalLength += msg.getContent().length();
            }
        }

        while (totalLength > MAX_TOTAL_LENGTH) {
            Message firstMessage = messagesHistory.poll();
            Message secondMessage = messagesHistory.poll();
            if (firstMessage != null && secondMessage != null) {
                totalLength -= (firstMessage.getContent().length() + secondMessage.getContent().length());
            }
        }

    }

}
