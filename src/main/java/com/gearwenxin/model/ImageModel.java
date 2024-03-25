package com.gearwenxin.model;

import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
public interface ImageModel<T extends ImageBaseRequest> {

    /**
     * 绘图
     *
     * @param imageBaseRequest 作图参数
     * @return ImageResponse 图片响应
     */
    Mono<ImageResponse> chatImage(T imageBaseRequest);

    Mono<ImageResponse> chatImage(T imageBaseRequest, float weight);

}
