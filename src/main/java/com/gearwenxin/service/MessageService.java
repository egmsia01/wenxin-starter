package com.gearwenxin.service;

import com.gearwenxin.entity.Message;

import java.util.Deque;

public interface MessageService {

    Deque<Message> getHistoryMessages(String id);

    void addHistoryMessage(String id, Message message);

    void addHistoryMessage(Deque<Message> messagesHistory, Message message);

}
