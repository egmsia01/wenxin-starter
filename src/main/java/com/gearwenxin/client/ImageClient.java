package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.images.ImageBot;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia

 */
@Slf4j
public abstract class ImageClient implements ImageBot<ImageBaseRequest>, BaseBot {

    @Override
    public Mono<ImageResponse> chatImage(ImageBaseRequest imageBaseRequest) {
        if (imageBaseRequest == null) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR);
        }
        imageBaseRequest.validSelf();

        log.info(getTag() + "imageRequest => {}", imageBaseRequest.toString());

        return ChatUtils.monoChatPost(
                getURL(), getCustomAccessToken(), imageBaseRequest, ImageResponse.class
        );
    }

}
