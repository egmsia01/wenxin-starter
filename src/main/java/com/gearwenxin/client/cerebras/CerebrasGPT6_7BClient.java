package com.gearwenxin.client.cerebras;

import com.gearwenxin.client.base.BaseClient;
import com.gearwenxin.config.WenXinProperties;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Ge Mingjia

 */
@Slf4j
@Lazy
@Service
public class CerebrasGPT6_7BClient extends BaseClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "Cerebras-GPT-6.7B-Client";

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    private String getCustomURL() {
        return wenXinProperties.getCustom_Model_URL();
    }

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
