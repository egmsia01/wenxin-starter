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

public class ChatUtils {

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

        validateMessageRule(updatedHistory, message);
        updatedHistory.offer(message);

        synchronizeHistories(messagesHistory, updatedHistory, message);

        if (message.getRole() == Role.assistant) {
            messagesHistory.clear();
            messagesHistory.addAll(updatedHistory);
            return;
        }

        handleExceedingLength(messagesHistory, updatedHistory);
    }

    private static void validateMessageRule(Deque<Message> history, Message message) {
        if (!history.isEmpty()) {
            Message lastMessage = history.peekLast();
            if (lastMessage != null) {
                // 如果当前是奇数位message，要求role值为user或function
                if (history.size() % 2 != 0) {
                    if (message.getRole() != Role.user && message.getRole() != Role.function) {
                        // 删除最后一条消息
                        history.pollLast();
                        validateMessageRule(history, message);
                    }
                } else {
                    // 如果当前是偶数位message，要求role值为assistant
                    if (message.getRole() != Role.assistant) {
                        // 删除最后一条消息
                        history.pollLast();
                        validateMessageRule(history, message);
                    }
                }

                // 第一个message的role不能是function
                if (history.size() == 1 && message.getRole() == Role.function) {
                    // 删除最后一条消息
                    history.pollLast();
                    validateMessageRule(history, message);
                }

                // 移除连续的相同role的user messages
                if (lastMessage.getRole() == Role.user && message.getRole() == Role.user) {
                    history.pollLast();
                    validateMessageRule(history, message);
                }
            }
        }
    }


    private static void synchronizeHistories(Deque<Message> original, Deque<Message> updated, Message message) {
        if (updated.size() <= original.size()) {
            updated.clear();
            updated.addAll(original);
            updated.offer(message);
        }
    }

    private static void handleExceedingLength(Deque<Message> messagesHistory, Deque<Message> updatedHistory) {
        int totalLength = updatedHistory.stream()
                .filter(msg -> msg.getRole() == Role.user)
                .mapToInt(msg -> msg.getContent().length())
                .sum();

        while (totalLength > MAX_TOTAL_LENGTH && updatedHistory.size() > 2) {
            Message firstMessage = updatedHistory.poll();
            Message secondMessage = updatedHistory.poll();
            if (firstMessage != null && secondMessage != null) {
                boolean b = (firstMessage.getRole() == Role.user || firstMessage.getRole() == Role.function) && secondMessage.getRole() == Role.assistant;
                if (b) {
                    totalLength -= (firstMessage.getContent().length() + secondMessage.getContent().length());
                }
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
