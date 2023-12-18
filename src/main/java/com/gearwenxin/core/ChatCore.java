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

        removeConsecutiveUserMessages(updatedHistory, message);
        updatedHistory.offer(message);

        synchronizeHistories(messagesHistory, updatedHistory, message);

        if (message.getRole() == Role.assistant) {
            messagesHistory.clear();
            messagesHistory.addAll(updatedHistory);
            return;
        }

        handleExceedingLength(messagesHistory, updatedHistory);
    }

    private static void removeConsecutiveUserMessages(Deque<Message> history, Message message) {
        if (!history.isEmpty()) {
            Message lastMessage = history.peekLast();
            if (lastMessage != null && lastMessage.getRole() == Role.user && message.getRole() == Role.user) {
                history.pollLast();
            }
        }
    }

    private static void synchronizeHistories(Deque<Message> original, Deque<Message> updated, Message message) {
        if (updated.size() <= original.size()) {
            updated.clear();
            updated.addAll(original);
        }
        updated.offer(message);
    }

    private static void handleExceedingLength(Deque<Message> messagesHistory, Deque<Message> updatedHistory) {
        int totalLength = updatedHistory.stream()
                .filter(msg -> msg.getRole() == Role.user)
                .mapToInt(msg -> msg.getContent().length())
                .sum();

        while (totalLength > MAX_TOTAL_LENGTH && updatedHistory.size() > 2) {
            Message firstMessage = updatedHistory.poll();
            Message secondMessage = updatedHistory.poll();
            if (firstMessage != null && secondMessage != null &&
                    (firstMessage.getRole() == Role.user || firstMessage.getRole() == Role.function) &&
                    secondMessage.getRole() == Role.assistant) {
                totalLength -= (firstMessage.getContent().length() + secondMessage.getContent().length());
            } else if (secondMessage != null) {
                updatedHistory.addFirst(secondMessage);
                totalLength -= secondMessage.getContent().length();
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
