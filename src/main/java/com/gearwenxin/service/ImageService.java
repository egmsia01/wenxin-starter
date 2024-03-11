package com.gearwenxin.service;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.core.WebManager;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.model.ImageModel;
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
public class ImageService {

    private final WebManager webManager = new WebManager();

    @Resource
    private WenXinProperties wenXinProperties;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    public Mono<ImageResponse> imageProcess(ImageBaseRequest imageBaseRequest, ModelConfig config) {
        assertNotNull(imageBaseRequest, "imageBaseRequest is null");
        imageBaseRequest.validSelf();

        return webManager.monoPost(config, getAccessToken(), imageBaseRequest, ImageResponse.class);
    }

}
