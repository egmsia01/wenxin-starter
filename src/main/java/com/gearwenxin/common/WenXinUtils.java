package com.gearwenxin.common;

import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.FunctionCall;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.enums.Role;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.entity.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.gearwenxin.common.Constant.MAX_TOTAL_LENGTH;

/**
 * @author Ge Mingjia
 * @date 2023/7/23
 */
public class WenXinUtils {

    private static final Object pollLock = new Object();

    @Deprecated
    public static Deque<Message> buildUserMessageDeque(String content) {
        return buildUserMessageHistory(content);
    }

    @Deprecated
    public static Deque<Message> buildUserMessageDeque(String content, String name, FunctionCall functionCall) {
        return buildUserMessageHistory(content, name, functionCall);
    }

    @Deprecated
    public static Deque<Message> buildMessageDeque(Message userMessage, Message assistantMessage) {
        return buildMessageHistory(userMessage, assistantMessage);
    }

    public static Deque<Message> buildUserMessageHistory(String content) {
        return buildUserMessageHistory(content, null, null);
    }

    // TODO:全局适配
    public static Deque<Message> buildUserMessageHistory(String content, String name, FunctionCall functionCall) {
        assertNotNull(content, "content is null");

        Deque<Message> messageHistory = new ConcurrentLinkedDeque<>();
        Message message = buildUserMessage(content, name, functionCall);
        messageHistory.offer(message);
        return messageHistory;
    }

    public static Deque<Message> buildMessageHistory(Message userMessage, Message assistantMessage) {
        Deque<Message> messageDeque = new ConcurrentLinkedDeque<>();
        offerMessage(messageDeque, userMessage);
        offerMessage(messageDeque, assistantMessage);
        return messageDeque;
    }

    public static Message buildUserMessage(String content) {
        return buildUserMessage(content, null, null);
    }

    // TODO:全局适配
    public static Message buildUserMessage(String content, String name, FunctionCall functionCall) {
        assertNotNull(content, "content is null");
        if (functionCall == null) {
            return new Message(Role.user, content, null, null);
        }
        assertNotNull(name, "content is null");

        return new Message(Role.user, content, name, functionCall);
    }

    // TODO:全局适配
    public static Message buildAssistantMessage(String content, String name, FunctionCall functionCall) {
        assertNotNull(content, "content is null");
        return new Message(Role.assistant, content, name, functionCall);
    }

    public static Message buildFunctionMessage(String name, String content) {
        assertNotNull(name, "name is null");
        assertNotNull(content, "content is null");

        return new Message(Role.function, content, name, null);
    }

    public static Message buildAssistantMessage(String content) {
        return buildAssistantMessage(content, null, null);
    }

    public static <T extends ChatBaseRequest> Object buildTargetRequest(Deque<Message> messagesHistory, boolean stream, T request) {
        Object targetRequest = null;
        if (request.getClass() == ChatBaseRequest.class) {
            BaseRequest.BaseRequestBuilder requestBuilder = ConvertUtils.toBaseRequest(request)
                    .stream(stream);
            if (messagesHistory != null)
                requestBuilder.messages(messagesHistory);
            targetRequest = requestBuilder.build();
        } else if (request.getClass() == ChatErnieRequest.class) {
            ErnieRequest.ErnieRequestBuilder requestBuilder = ConvertUtils.toErnieRequest((ChatErnieRequest) request)
                    .stream(stream);
            if (messagesHistory != null)
                requestBuilder.messages(messagesHistory);
            targetRequest = requestBuilder.build();
        }
        return targetRequest;
    }

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

        synchronized (messagesHistory) {
            Message lastMessage = messagesHistory.peekLast();
            if (lastMessage != null && lastMessage.getRole() == Role.user &&
                    message.getRole() == Role.user) {
                messagesHistory.pollLast();
            }
            messagesHistory.offer(message);

            if (message.getRole() == Role.assistant) {
                return;
            }

            // 统计用户消息总长度
            int totalLength = 0;
            for (Message msg : messagesHistory) {
                if (msg.getRole() == Role.user) {
                    totalLength += msg.getContent().length();
                }
            }

            // 大于最大长度后删除最前面的对话
            while (totalLength > MAX_TOTAL_LENGTH && messagesHistory.size() >= 2) {
                Message firstMessage = messagesHistory.poll();
                Message secondMessage = messagesHistory.poll();
                if (firstMessage != null && secondMessage != null) {
                    if (firstMessage.getRole() == Role.user && secondMessage.getRole() == Role.assistant) {
                        totalLength -= (firstMessage.getContent().length() + secondMessage.getContent().length());
                    }
                } else {
                    // 处理失败，将消息重新放回队列
                    if (firstMessage != null) {
                        messagesHistory.addFirst(firstMessage);
                    }
                    if (secondMessage != null) {
                        messagesHistory.addFirst(secondMessage);
                    }
                }
            }
        }

    }


    public static void assertNotBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, message);
        }
    }

    public static void assertNotBlankMono(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, message);
        }
    }

    public static void assertNotBlank(String message, String... strings) {
        for (String str : strings) {
            if (StringUtils.isBlank(str)) {
                throw new WenXinException(ErrorCode.PARAMS_ERROR, message);
            }
        }
    }

    public static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, message);
        }
    }

}
