package com.gearwenxin.client;

import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.model.BaseBot;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Ge Mingjia

 */
@Slf4j
@Service
public class EmbeddingV1Client implements BaseBot {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "EmbeddingV1Client";

    private static final String URL = Constant.PROMPT_URL;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
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
        return URL;
    }

    @Override
    public String getTag() {
        return TAG;
    }

}
