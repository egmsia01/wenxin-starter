package com.gearwenxin.client.cerebras;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.entity.Message;

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/8/5
 */
public abstract class CerebrasGPT13BClient extends FullClient {

    protected CerebrasGPT13BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "Cerebras-GPT-13B-Client";
    private static Map<String, Deque<Message>> CEREBRAS_GPT_13B_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return CEREBRAS_GPT_13B_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        CEREBRAS_GPT_13B_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return getCustomURL();
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
