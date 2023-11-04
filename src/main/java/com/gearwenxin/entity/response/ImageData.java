package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 表示生成图片的详细信息。
 */
@Data
public class ImageData {

    /**
     * 固定值 "image"，表示图像。
     */
    private String object;

    /**
     * 图片base64编码内容。
     */
    @JsonProperty("b64_image")
    private String b64Image;

    /**
     * 图片序号。
     */
    private int index;

}
