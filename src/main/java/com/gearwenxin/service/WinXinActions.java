package com.gearwenxin.service;

import com.gearwenxin.entity.Message;

import java.util.Deque;
import java.util.Map;

public interface WinXinActions {

    void initMessageMap(Map<String, Deque<Message>> map);

    void initMessages(String msgUid, Deque<Message> messageDeque);

    String exportMessages(String msgUid);

    String exportAllMessages();

    boolean interpretChat(String msgUid);

}
