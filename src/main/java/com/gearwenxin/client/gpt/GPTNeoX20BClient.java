package com.gearwenxin.client.gpt;

import com.gearwenxin.client.base.BaseClient;

/**
 * @author Ge Mingjia

 */
public abstract class GPTNeoX20BClient extends BaseClient {

    protected GPTNeoX20BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "GPT-NeoX-20B-Client";

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
