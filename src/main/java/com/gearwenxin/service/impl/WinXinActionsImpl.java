package com.gearwenxin.service.impl;

import com.gearwenxin.entity.Message;
import com.gearwenxin.service.ChatService;
import com.gearwenxin.service.WinXinActions;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.Map;

@Slf4j
@Service
public class WinXinActionsImpl implements WinXinActions {

    public static final String TAG = "WinXinActions";

    @Resource
    private ChatService chatService;
    public static final Gson gson = new Gson();

    @Override
    public void initMessageMap(Map<String, Deque<Message>> map) {
        chatService.setChatMessageHistoryMap(map);
    }

    @Override
    public void initMessages(String msgUid, Deque<Message> messageDeque) {
        chatService.getChatMessageHistoryMap().put(msgUid, messageDeque);
    }

    @Override
    public String exportMessages(String msgUid) {
        Deque<Message> messages = chatService.getChatMessageHistoryMap().get(msgUid);
        if (messages != null) {
            log.debug("[{}] export messages, magUid: {}", TAG, msgUid);
            return gson.toJson(messages);
        }
        return null;
    }

    @Override
    public String exportAllMessages() {
        Map<String, Deque<Message>> chatMessageHistoryMap = chatService.getChatMessageHistoryMap();
        if (chatMessageHistoryMap != null) {
            log.debug("[{}] export all messages", TAG);
            return gson.toJson(chatMessageHistoryMap);
        }
        return null;
    }

}
