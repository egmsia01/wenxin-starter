package com.gearwenxin.client;

import com.gearwenxin.core.ChatCore;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.images.ImageBot;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.gearwenxin.common.WenXinUtils.assertNotBlank;
import static com.gearwenxin.common.WenXinUtils.assertNotNull;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
public abstract class ImageClient implements ImageBot<ImageBaseRequest>, BaseBot {

    @Override
    public Mono<ImageResponse> chatImage(ImageBaseRequest imageBaseRequest) {
        assertNotNull(imageBaseRequest, "imageBaseRequest is null");
        imageBaseRequest.validSelf();
        log.info(getTag() + "imageRequest => {}", imageBaseRequest);

        return ChatCore.monoChatPost(
                getURL(), getCustomAccessToken(), imageBaseRequest, ImageResponse.class
        );
    }

}
