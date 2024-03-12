package com.gearwenxin.common;

import com.gearwenxin.core.ChatUtils;
import com.gearwenxin.entity.FunctionCall;
import com.gearwenxin.entity.enums.Role;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.entity.Message;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/23
 */
public class WenXinUtils {

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
        ChatUtils.addMessage(messageDeque, userMessage);
        ChatUtils.addMessage(messageDeque, assistantMessage);
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

    public static Mono<Void> assertNotNullMono(ErrorCode errorCode, String message, Object... obj) {
        for (Object o : obj) {
            if (o == null) {
                return Mono.error(() -> new WenXinException(errorCode, message));
            }
            if (o instanceof String) {
                if (StringUtils.isBlank((String) o)) {
                    return Mono.error(() -> new WenXinException(errorCode, message));
                }
            }
        }
        return Mono.empty();
    }

    public static Mono<Void> assertNotBlankMono(String message, String... strings) {
        for (String str : strings) {
            if (StringUtils.isBlank(str)) {
                return Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR, message));
            }
        }
        return Mono.empty();
    }

}
