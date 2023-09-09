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
public abstract class GPT4AllJClient extends SingleClient {

    protected GPT4AllJClient() {
    }

    private String accessToken = null;
    private static final String TAG = "GPT4All-J-Client_";

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
