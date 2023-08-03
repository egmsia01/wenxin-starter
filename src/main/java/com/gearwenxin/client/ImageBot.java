package com.gearwenxin.client;

import com.gearwenxin.model.chatmodel.ChatVilGCRequest;
import com.gearwenxin.model.response.VilGCResponse;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface ImageBot {

    /**
     * 绘图
     *
     * @param chatVilGCRequest 作图参数
     * @return byte[] 图片的字节数组
     */
    VilGCResponse chatImage(ChatVilGCRequest chatVilGCRequest);

}
