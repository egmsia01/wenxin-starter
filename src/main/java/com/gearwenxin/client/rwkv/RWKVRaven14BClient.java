package com.gearwenxin.client.rwkv;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.entity.Message;

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class RWKVRaven14BClient extends FullClient {

    protected RWKVRaven14BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "RWKV-Raven-14B-Client";
    private static Map<String, Deque<Message>> RWKV_RAVEN_14B_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return RWKV_RAVEN_14B_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        RWKV_RAVEN_14B_MESSAGES_HISTORY_MAP = map;
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
