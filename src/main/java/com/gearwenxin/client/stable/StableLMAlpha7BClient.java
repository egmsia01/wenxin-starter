package com.gearwenxin.client.stable;

import com.gearwenxin.client.base.BaseClient;

/**
 * @author Ge Mingjia
 * @date 2023/8/5
 */
public abstract class StableLMAlpha7BClient extends BaseClient {

    protected StableLMAlpha7BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "StableLM-Alpha-7B-Client";

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
