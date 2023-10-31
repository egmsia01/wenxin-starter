package com.gearwenxin.client.pythia;

import com.gearwenxin.client.base.BaseClient;

/**
 * @author Ge Mingjia

 */
public abstract class Pythia12BClient extends BaseClient {

    protected Pythia12BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "Pythia-12B-Client";

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
