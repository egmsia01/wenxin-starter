package com.gearwenxin.client;

import com.gearwenxin.client.base.BaseClient;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class StarCoderClient extends BaseClient {

    protected StarCoderClient() {
    }

    private String accessToken = null;
    private static final String TAG = "StarCoder-Client";

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
