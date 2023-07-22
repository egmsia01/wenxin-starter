package com.gearwenxin.client.erniebot;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.BaseRequest;
import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ErnieResponse;
import com.gearwenxin.model.Message;
import com.google.gson.Gson;
import com.gearwenxin.model.erniebot.ErnieRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */

@Slf4j
public class ErnieBotClient implements ErnieBot {

    private final String accessToken;
    static final Gson GSON = new Gson();

    // 存储不同msgCode的对话历史
    private final Map<String, List<Message>> messagesHistoryMap = new HashMap<>();

    public ErnieBotClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ErnieResponse chatSingleRound(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Message> messageList = buildMessageList(content);
        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messageList);
        log.info("content_singleErnieRequest => {}", ernieRequest.toString());

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));

        return GSON.fromJson(response, ErnieResponse.class);
    }

    @Override
    public ErnieResponse chatSingleRound(ChatErnieRequest chatErnieRequest) {
        this.validChatErnieRequest(chatErnieRequest);

        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        log.info("singleRequest => {}", ernieRequest.toString());

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));

        return GSON.fromJson(response, ErnieResponse.class);
    }

    @Override
    public ErnieResponse chatMultipleRounds(String content, String msgUID) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(msgUID)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Message> messagesHistory = messagesHistoryMap.computeIfAbsent(msgUID, k -> new ArrayList<>());
        messagesHistory.add(buildUserMessage(content));

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info("content_multipleErnieRequest => {}", ernieRequest.toString());

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));
        ErnieResponse ernieResponse = GSON.fromJson(response, ErnieResponse.class);
        messagesHistory.add(buildAssistantMessage(ernieResponse.getResult()));

        return ernieResponse;
    }

    @Override
    public ErnieResponse chatMultipleRounds(ChatErnieRequest chatErnieRequest, String msgUID) {
        if (StringUtils.isEmpty(msgUID)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        List<Message> messagesHistory = messagesHistoryMap.computeIfAbsent(msgUID, key -> new ArrayList<>());
        // 添加到历史
        messagesHistory.add(buildUserMessage(chatErnieRequest.getContent()));
        ernieRequest.setMessages(messagesHistory);
        log.info("multipleErnieRequest => {}", ernieRequest.toString());

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));
        ErnieResponse ernieResponse = GSON.fromJson(response, ErnieResponse.class);
        messagesHistory.add(buildAssistantMessage(ernieResponse.getResult()));

        return ernieResponse;
    }

    @Override
    public void validChatErnieRequest(ChatErnieRequest request) {

        // 检查content不为空
        if (StringUtils.isEmpty(request.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查temperature和topP不both有值
        if (request.getTemperature() != null && request.getTopP() != null) {
            log.warn("Temperature and topP cannot both have value");
        }
        // 检查temperature范围
        if (request.getTemperature() != null &&
                (request.getTemperature() <= 0 || request.getTemperature() > 1.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "temperature should be in (0, 1]");
        }
        // 检查topP范围
        if (request.getTopP() != null &&
                (request.getTopP() < 0 || request.getTopP() > 1.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "topP should be in [0, 1]");
        }
        // 检查penaltyScore范围
        if (request.getTemperature() != null &&
                (request.getPenaltyScore() < 1.0 || request.getPenaltyScore() > 2.0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "penaltyScore should be in [1, 2]");
        }
    }

    private List<Message> buildMessageList(String content) {
        List<Message> messageList = new ArrayList<>();
        messageList.add(buildUserMessage(content));
        return messageList;
    }

    private Message buildUserMessage(String content) {
        return new Message(RoleEnum.user, content);
    }

    private Message buildAssistantMessage(String content) {
        return new Message(RoleEnum.assistant, content);
    }

}
