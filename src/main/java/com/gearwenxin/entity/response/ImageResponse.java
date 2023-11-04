package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.entity.Usage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ge Mingjia
 * @date 2023/8/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {

    /**
     * 请求的ID。
     */
    private String id;

    /**
     * 回包类型。固定值为 "image"，表示图像生成返回。
     */
    private String object;

    /**
     * 时间戳，表示生成响应的时间。
     */
    private int created;

    /**
     * 生成图片结果列表。
     */
    private List<ImageData> data;

    /**
     * token统计信息，token数 = 汉字数 + 单词数 * 1.3 （仅为估算逻辑）。
     */
    private Usage usage;

    /**
     * 错误代码，正常为 null
     */
    @JsonProperty("error_code")
    private Integer errorCode;

    /**
     * 错误信息，正常为 null
     */
    @JsonProperty("error_msg")
    private String errorMsg;

}
