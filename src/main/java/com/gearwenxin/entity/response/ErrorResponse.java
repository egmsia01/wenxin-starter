package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/10/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 本轮对话的id
     */
    @JsonProperty("id")
    private String id;

    /**
     * 用于定位的log_id
     */
    @JsonProperty("log_id")
    private String logId;

    /**
     * 错误代码，正常为 null
     */
    @JsonProperty("error_code")
    private Integer errorCode;

    /**
     * 错误代码，正常为 null
     */
    @JsonProperty("eb_code")
    private Integer ebCode;

    /**
     * 错误信息，正常为 null
     */
    @JsonProperty("error_msg")
    private String errorMsg;

}
