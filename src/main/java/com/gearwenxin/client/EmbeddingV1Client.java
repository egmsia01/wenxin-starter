package com.gearwenxin.client;

import com.gearwenxin.common.Constant;
import com.gearwenxin.model.BaseBot;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class EmbeddingV1Client implements BaseBot {

    protected EmbeddingV1Client() {
    }

    private String accessToken = null;
    private static final String TAG = "PromptBotClient";

    private static final String URL = Constant.PROMPT_URL;

    protected abstract String getAccessToken();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getURL() {
        return URL;
    }

}
