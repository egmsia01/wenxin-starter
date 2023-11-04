package com.gearwenxin.client.stable;

import com.gearwenxin.client.ImageClient;
import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
@Service
public class StableDiffusionXLClient extends ImageClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "StableDiffusionXLClient";
    private static final String URL = Constant.STABLE_DIFFUSION_XL_URL;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    private String getCustomURL() {
        return URL;
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
