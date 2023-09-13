package com.gearwenxin.client.cerebras;

import com.gearwenxin.client.base.BaseClient;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class CerebrasGPT6_7BClient extends BaseClient {

    protected CerebrasGPT6_7BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "Cerebras-GPT-6.7B-Client";

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
