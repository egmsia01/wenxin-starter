package com.gearwenxin.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * @date 2023/8/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VilGCRequest {

    /**
     * 提示词
     */
    private String content;

    /**
     * 宽 px
     */
    private int width;

    /**
     * 高 px
     */
    private int height;

}
