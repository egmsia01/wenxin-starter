package com.gearwenxin.model.images;

import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia

 */
public interface ImageBot<T extends ImageBaseRequest> {

    /**
     * 绘图
     *
     * @param imageBaseRequest 作图参数
     * @return ImageResponse 图片响应
     */
    Mono<ImageResponse> chatImage(T imageBaseRequest);

}
