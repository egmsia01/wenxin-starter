package com.gearwenxin.client.ernie;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.common.*;
import com.gearwenxin.entity.Message;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotTurboClient extends FullClient {

    protected ErnieBotTurboClient() {
    }

    private String accessToken = null;
    private static final String TAG = "Ernie-Bot-Turbo-Client";
    private static final String URL = Constant.ERNIE_BOT_TURBO_URL;

    private static Map<String, Queue<Message>> TURBO_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    protected abstract String getAccessToken();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return TURBO_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        TURBO_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public String getTag() {
        return TAG;
    }

}
