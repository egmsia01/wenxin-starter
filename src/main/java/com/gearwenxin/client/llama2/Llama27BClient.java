package com.gearwenxin.client.llama2;

import com.gearwenxin.client.DefaultParamsClient;
import com.gearwenxin.entity.Message;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class Llama27BClient extends DefaultParamsClient {

    protected Llama27BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "Llama2-7B-Client_";
    private static Map<String, Queue<Message>> LLAMA2_7B_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return LLAMA2_7B_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        LLAMA2_7B_MESSAGES_HISTORY_MAP = map;
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
