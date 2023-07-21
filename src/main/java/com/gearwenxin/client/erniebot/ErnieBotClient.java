package com.gearwenxin.client.erniebot;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.RoleEnum;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ErnieResponse;
import com.gearwenxin.model.Message;
import com.google.gson.Gson;
import com.gearwenxin.model.erniebot.ErnieRequest;
import lombok.extern.slf4j.Slf4j;

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
    public ErnieResponse chatWithSingleRound(String content) {
        List<Message> messageList = new ArrayList<>();
        ErnieRequest ernieRequest = new ErnieRequest();

        Message message = new Message();
        message.setRole(RoleEnum.user);
        message.setContent(content);
        messageList.add(message);

        ernieRequest.setMessages(messageList);

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));

        return GSON.fromJson(response, ErnieResponse.class);
    }

    @Override
    public ErnieResponse chatWithSingleRound(ChatErnieRequest chatErnieRequest) {
        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));

        return GSON.fromJson(response, ErnieResponse.class);
    }

    @Override
    public ErnieResponse chatMultipleRounds(String content, String msgUid) {
        List<Message> messagesHistory = messagesHistoryMap.computeIfAbsent(msgUid, key -> new ArrayList<>());
        // 添加到历史
        messagesHistory.add(new Message(RoleEnum.user, content));

        ErnieRequest ernieRequest = new ErnieRequest();
        ernieRequest.setMessages(messagesHistory);
        log.info("ernieRequest => {}", GSON.toJson(ernieRequest));

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));
        ErnieResponse ernieResponse = GSON.fromJson(response, ErnieResponse.class);
        messagesHistory.add(new Message(RoleEnum.assistant, ernieResponse.getResult()));

        return ernieResponse;
    }

    @Override
    public ErnieResponse chatMultipleRounds(ChatErnieRequest chatErnieRequest, String msgUid) {
        ErnieRequest ernieRequest = ConvertUtils.chatErnieRequestToErnieRequest(chatErnieRequest);
        List<Message> messagesHistory = messagesHistoryMap.computeIfAbsent(msgUid, key -> new ArrayList<>());
        // 添加到历史
        messagesHistory.add(new Message(RoleEnum.user, chatErnieRequest.getContent()));

        ernieRequest.setMessages(messagesHistory);
        log.info("ernieRequest => {}", GSON.toJson(ernieRequest));

        String response = ChatUtils.commonChat(URLConstant.ERNIE_BOT_URL, accessToken, GSON.toJson(ernieRequest));
        ErnieResponse ernieResponse = GSON.fromJson(response, ErnieResponse.class);
        messagesHistory.add(new Message(RoleEnum.assistant, ernieResponse.getResult()));

        return ernieResponse;
    }

}
