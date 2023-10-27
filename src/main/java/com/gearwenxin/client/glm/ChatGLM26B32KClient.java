package com.gearwenxin.client.glm;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.common.Constant;
import com.gearwenxin.entity.Message;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class ChatGLM26B32KClient extends FullClient {

    protected ChatGLM26B32KClient() {
    }

    private String accessToken = null;
    private static final String TAG = "ChatGLM2-6B-32K-Client";
    private static final String URL = Constant.CHAT_GLM_6B_32K_URL;
    private static Map<String, Deque<Message>> CHAT_GLM2_6B_32K_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    protected abstract String getAccessToken();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return CHAT_GLM2_6B_32K_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        CHAT_GLM2_6B_32K_MESSAGES_HISTORY_MAP = map;
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
    public String getTag() {
        return TAG;
    }
}
