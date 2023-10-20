package com.gearwenxin.exception;

import com.gearwenxin.common.ErrorCode;

/**
 * @author Ge Mingjia
 * @date 2023/10/20
 */
@Deprecated
public class BusinessException extends RuntimeException {

    public BusinessException(int code, String message) {
        throw new WenXinException(code, message);
    }

    public BusinessException(ErrorCode errorCode) {
        throw new WenXinException(errorCode);
    }

    public BusinessException(ErrorCode errorCode, String message) {
        throw new WenXinException(errorCode, message);
    }

}
