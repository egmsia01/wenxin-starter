package com.gearwenxin.client.stable;

import com.gearwenxin.client.ImageClient;
import com.gearwenxin.config.WenXinProperties;
import javax.annotation.Resource;
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
public class StableDiffusionV1_5Client extends ImageClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;

    private static final String TAG = "Stable-Diffusion-V1_5-Client";

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    private String getCustomURL() {
        return wenXinProperties.getStable_Diffusion_V1_5_URL();
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
