package com.gearwenxin.model;

import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ImageResponse;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ImageBot<T extends ImageBaseRequest> {

    /**
     * 绘图
     *
     * @param imageBaseRequest 作图参数
     * @return ImageResponse 图片响应
     */
    ImageResponse chatImage(T imageBaseRequest);

}
