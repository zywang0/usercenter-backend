package com.usercenter.usercenterbackend.common;

public class ResultUtils {
    public static <T> BaseResponse ok(T data) {
        return new BaseResponse(0, data, "ok");
    }

    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(int code, String msg, String description) {
        return new BaseResponse<>(code, null, msg, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String msg, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, msg, description);
    }

    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMsg(), description);
    }

}
