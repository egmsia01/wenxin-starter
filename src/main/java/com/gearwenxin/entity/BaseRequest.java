package com.gearwenxin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Deque;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 聊天上下文信息.
     * （1）messages成员不能为空，1个成员表示单轮对话，多个成员表示多轮对话
     * （2）最后一个message为当前请求的信息，前面的message为历史对话信息
     * （3）必须为奇数个成员，成员中message的role必须依次为user、assistant
     * （4）最后一个message的content长度（即此轮对话的问题）不能超过2000个字符；
     * 如果messages中content总长度大于2000字符，系统会依次遗忘最早的历史会话，直到content的总长度不超过2000个字符
     */
    @JsonProperty("messages")
    private Deque<Message> messages;

    /**
     * 是否以流式接口的形式返回数据，默认false
     */
    @JsonProperty("stream")
    private Boolean stream;

    public static class BaseRequestBuilder {
        private String userId;
        private Deque<Message> messages;
        private Boolean stream;

        public BaseRequestBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public BaseRequestBuilder messages(Deque<Message> messages) {
            this.messages = messages;
            return this;
        }

        public BaseRequestBuilder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        public BaseRequest build() {
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setUserId(userId);
            baseRequest.setMessages(messages);
            baseRequest.setStream(stream);

            return baseRequest;
        }
    }

    public static BaseRequestBuilder builder() {
        return new BaseRequestBuilder();
    }
}
