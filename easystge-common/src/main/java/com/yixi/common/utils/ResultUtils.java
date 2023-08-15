package com.yixi.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResultUtils {
    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok");
    }

    public static BaseResponse error(EventCode eventCode){
        return new BaseResponse<>(eventCode);
    }

    public static BaseResponse error(int code, String message, String description){
        return new BaseResponse<>(code, null, message, description);
    }

    public static BaseResponse error(EventCode eventCode, String message, String description){
        return new BaseResponse<>(eventCode.getCode(), null, message, description);
    }

    public static BaseResponse error(EventCode eventCode, String description){
        return new BaseResponse<>(eventCode.getCode(), null, eventCode.getMessage(), description);
    }


}
