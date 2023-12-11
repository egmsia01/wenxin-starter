package com.gearwenxin.core;

import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.enums.Role;
import com.gearwenxin.entity.request.ErnieRequest;

import java.util.Deque;
import java.util.LinkedList;

import static com.gearwenxin.common.Constant.MAX_TOTAL_LENGTH;
import static com.gearwenxin.common.WenXinUtils.assertNotBlank;
import static com.gearwenxin.common.WenXinUtils.assertNotNull;

public class ChatCore {

    /**
     * 向历史消息中添加消息
     *
     * @param messagesHistory 历史消息队列
     * @param message         需添加的Message
     */
    public static void offerMessage(Deque<Message> messagesHistory, Message message) {
        assertNotNull(messagesHistory, "messagesHistory is null");
        assertNotNull(message, "message is null");
        assertNotBlank(message.getContent(), "message.content is null or blank");

        Deque<Message> updatedHistory = new LinkedList<>(messagesHistory);
        // 在副本上进行修改
        if (!updatedHistory.isEmpty()) {
            Message lastMessage = updatedHistory.peekLast();
            if (lastMessage.getRole() == Role.user && message.getRole() == Role.user) {
                updatedHistory.pollLast();
            }
        }
        updatedHistory.offer(message);
        /*
          如果创建的时候获取到了刚 clear() 但是还没有 addAll() 的 messageHistory
          那么一定满足 updatedHistory.size() < messagesHistory.size()
          此时重新同步两个队列的消息
         */
        if (updatedHistory.size() <= messagesHistory.size()) {
            updatedHistory.clear();
            updatedHistory.addAll(messagesHistory);
            updatedHistory.offer(message);
        }
        if (message.getRole() == Role.assistant) {
            messagesHistory.clear();
            messagesHistory.addAll(updatedHistory);
            return;
        }

        // 统计用户消息总长度
        int totalLength = 0;
        for (Message msg : updatedHistory) {
            if (msg.getRole() == Role.user) {
                totalLength += msg.getContent().length();
            }
        }
        // 大于最大长度后删除最前面的对话
        while (totalLength > MAX_TOTAL_LENGTH && updatedHistory.size() > 2) {
            Message firstMessage = updatedHistory.poll();
            Message secondMessage = updatedHistory.poll();
            if (firstMessage != null && secondMessage != null) {
                if ((firstMessage.getRole() == Role.user || firstMessage.getRole() == Role.function)
                        && secondMessage.getRole() == Role.assistant) {
                    totalLength -= (firstMessage.getContent().length() + secondMessage.getContent().length());
                }
            } else {
                // 处理失败，将消息重新放回队列
                if (firstMessage != null) {
                    updatedHistory.addFirst(firstMessage);
                }
                if (secondMessage != null) {
                    updatedHistory.addFirst(secondMessage);
                }
            }
        }
        messagesHistory.clear();
        messagesHistory.addAll(updatedHistory);
    }

    public static <T extends ChatBaseRequest> Object buildTargetRequest(Deque<Message> messagesHistory, boolean stream, T request) {
        Object targetRequest = null;
        if (request.getClass() == ChatBaseRequest.class) {
            BaseRequest.BaseRequestBuilder requestBuilder = ConvertUtils.toBaseRequest(request).stream(stream);
            if (messagesHistory != null) requestBuilder.messages(messagesHistory);
            targetRequest = requestBuilder.build();
        } else if (request.getClass() == ChatErnieRequest.class) {
            ErnieRequest.ErnieRequestBuilder requestBuilder = ConvertUtils.toErnieRequest((ChatErnieRequest) request).stream(stream);
            if (messagesHistory != null) requestBuilder.messages(messagesHistory);
            targetRequest = requestBuilder.build();
        }
        return targetRequest;
    }
}
