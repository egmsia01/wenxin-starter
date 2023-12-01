package com.gearwenxin.exception;

import com.gearwenxin.common.ErrorCode;
import lombok.Getter;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/22
 */
@Getter
public class WenXinException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public WenXinException(int code, String message) {
        super(message);
        this.code = code;
    }

    public WenXinException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public WenXinException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
