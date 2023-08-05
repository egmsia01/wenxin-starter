package com.gearwenxin.client.stable;

import com.gearwenxin.client.ImageClient;
import com.gearwenxin.entity.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class StableDiffusionV1_5Client extends ImageClient {

    protected StableDiffusionV1_5Client() {
    }

    private String accessToken = null;

    private static final String TAG = "Stable-Diffusion-V1_5-Client_";

    // 获取access-token
    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        log.error(TAG + "StableDiffusionV1_5Client not have MessageHistoryMap");
        return Collections.emptyMap();
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        log.error(TAG + "StableDiffusionV1_5Client not need init");
    }

    @Override
    public String getURL() {
        return getCustomURL();
    }

    @Override
    public String getTag() {
        return TAG;
    }

}
