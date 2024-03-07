package com.gearwenxin.service;

import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.core.WebManager;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.images.ImageBot;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.gearwenxin.common.WenXinUtils.assertNotNull;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Service
public class ImageService implements ImageBot<ImageBaseRequest>, BaseBot {

    WebManager webManager = new WebManager();

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "ErnieBotVilGClient";

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    private String getCustomURL() {
        return null;
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

    @Override
    public Mono<ImageResponse> chatImage(ImageBaseRequest imageBaseRequest) {
        assertNotNull(imageBaseRequest, "imageBaseRequest is null");
        imageBaseRequest.validSelf();
        log.info(getTag() + "imageRequest => {}", imageBaseRequest);

        return webManager.monoPost(
                null, getCustomAccessToken(), imageBaseRequest, ImageResponse.class
        );
    }

}
