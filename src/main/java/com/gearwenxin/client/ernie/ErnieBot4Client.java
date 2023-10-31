package com.gearwenxin.client.ernie;

import com.gearwenxin.common.*;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.ContBot;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia

 */
@Slf4j
public abstract class ErnieBot4Client extends ErnieBotClient implements ContBot<ChatErnieRequest>, BaseBot {

    protected ErnieBot4Client() {
    }

    private String accessToken = null;
    private static final String TAG = "ErnieBot4Client";

    private static Map<String, Deque<Message>> ERNIE_4_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    private static final String URL = Constant.ERNIE_BOT_4_URL;

    protected abstract String getAccessToken();

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return ERNIE_4_MESSAGES_HISTORY_MAP;
    }

    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        ERNIE_4_MESSAGES_HISTORY_MAP = map;
    }

}
