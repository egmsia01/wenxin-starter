package com.gearwenxin.common;

import com.gearwenxin.entity.RoleEnum;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.entity.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.Deque;

import static com.gearwenxin.common.Constant.MAX_TOTAL_LENGTH;

/**
 * @author Ge Mingjia
 * @date 2023/7/23
 */
public class WenXinUtils {

    public static Deque<Message> buildUserMessageDeque(String content) {
        if (content == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is null");
        }
        Deque<Message> messageDeque = new LinkedList<>();
        Message message = buildUserMessage(content);
        messageDeque.offer(message);
        return messageDeque;
    }

    public static Deque<Message> buildMessageDeque(Message userMessage, Message assistantMessage) {
        Deque<Message> messageDeque = new LinkedList<>();
        offerMessage(messageDeque, userMessage);
        offerMessage(messageDeque, assistantMessage);
        return messageDeque;
    }

    public static Message buildUserMessage(String content) {
        if (content == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is null");
        }

        return new Message(RoleEnum.user, content);
    }

    public static Message buildAssistantMessage(String content) {
        if (content == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is null");
        }
        return new Message(RoleEnum.assistant, content);
    }

    /**
     * 向历史消息中添加消息
     *
     * @param messagesHistory 历史消息队列
     * @param message         需添加的Message
     */
    public static void offerMessage(Deque<Message> messagesHistory, Message message) {

        if (messagesHistory == null || message == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(message.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "message is null!");
        }

        Message lastMessage = messagesHistory.peekLast();
        if (lastMessage != null && lastMessage.getRole() == RoleEnum.user &&
                message.getRole() == RoleEnum.user) {
            // 移除上一条未回复的问题
            messagesHistory.pollLast();
        }
        messagesHistory.offer(message);

        if (message.getRole() == RoleEnum.assistant) {
            return;
        }

        // 统计用户消息总长度
        int totalLength = 0;
        for (Message msg : messagesHistory) {
            if (msg.getRole() == RoleEnum.user) {
                totalLength += msg.getContent().length();
            }
        }

        // 大于最大长度后删除最前面的对话
        while (totalLength > MAX_TOTAL_LENGTH) {
            Message firstMessage = messagesHistory.poll();
            Message secondMessage = messagesHistory.poll();
            if (firstMessage != null && secondMessage != null) {
                totalLength -= (firstMessage.getContent().length() + secondMessage.getContent().length());
            }
        }

    }

}
