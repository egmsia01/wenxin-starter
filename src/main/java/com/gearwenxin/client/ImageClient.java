package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.ImageBot;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Queue;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ImageClient implements ImageBot<ImageBaseRequest> {

    public abstract String getCustomAccessToken();

    public abstract void setCustomAccessToken(String accessToken);

    public abstract Map<String, Queue<Message>> getMessageHistoryMap();

    public abstract void initMessageHistoryMap(Map<String, Queue<Message>> map);

    public abstract String getURL();

    public abstract String getTag();

    @Override
    public ImageResponse chatImage(ImageBaseRequest imageBaseRequest) {
        if (imageBaseRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        imageBaseRequest.validSelf();

        log.info(getTag() + "imageRequest => {}", imageBaseRequest.toString());

        return ChatUtils.monoPost(
                        getURL(),
                        getCustomAccessToken(),
                        imageBaseRequest,
                        ImageResponse.class)
                .block();
    }

}
