package com.yixi.common.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    
    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message,"");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "","");
    }

    public BaseResponse(EventCode eventCode){
        this(eventCode.getCode(), null, eventCode.getMessage(), eventCode.getDescription());
    }


}
