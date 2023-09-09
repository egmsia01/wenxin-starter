package com.gearwenxin.client.gpt;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.client.base.SingleClient;
import com.gearwenxin.entity.Message;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/8/5
 */
public abstract class GPTJ6BClient extends SingleClient {

    protected GPTJ6BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "GPT-J-6B-Client_";

    protected abstract String getAccessToken();

    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
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
