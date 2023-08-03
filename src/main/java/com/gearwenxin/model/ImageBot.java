package com.gearwenxin.model;

import com.gearwenxin.entity.response.VilGCResponse;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ImageBot<T> {

    /**
     * 绘图
     *
     * @param chatRequest 作图参数
     * @return byte[] 图片的字节数组
     */
    VilGCResponse chatImage(T chatRequest);

}
