package com.gearwenxin.client.glm;

import com.gearwenxin.client.ImageClient;
import com.gearwenxin.config.WenXinProperties;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Lazy
@Service
public class VisualGLM6BClient extends ImageClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;

    private static final String TAG = "VisualGLM-6B-Client";

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    private String getCustomURL() {
        return wenXinProperties.getVisual_GLM_6B_URL();
    }

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
        return getCustomURL();
    }

    @Override
    public String getTag() {
        return TAG;
    }

}
