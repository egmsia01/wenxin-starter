package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class VilGCResponse {

    /**
     * base64编码的图片
     */
    private List<String> images;

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
