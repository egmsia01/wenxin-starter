package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.enums.SamplerType;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.images.ImageBot;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.gearwenxin.common.WenXinUtils.assertNotBlank;
import static com.gearwenxin.common.WenXinUtils.assertNotNull;

/**
 * @author Ge Mingjia

 */
@Slf4j
public abstract class ImageClient implements ImageBot<ImageBaseRequest>, BaseBot {

    @Override
    public Mono<ImageResponse> chatImage(ImageBaseRequest imageBaseRequest) {
        assertNotNull(imageBaseRequest, "imageBaseRequest is null");
        imageBaseRequest.validSelf();
        log.info(getTag() + "imageRequest => {}", imageBaseRequest);

        return ChatUtils.monoChatPost(
                getURL(), getCustomAccessToken(), imageBaseRequest, ImageResponse.class
        );
    }

}
