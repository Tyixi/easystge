package com.yixi.common.exception;

import com.yixi.common.utils.EventCode;
import lombok.Getter;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */
@Getter
public class BusinessException extends RuntimeException{

    private int code; // 业务状态码
    private final String description;

    public BusinessException(String message, int code,String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(EventCode eventCode){
        super(eventCode.getMessage());
        this.code = eventCode.getCode();
        this.description = eventCode.getDescription();
    }

    public BusinessException(EventCode eventCode, String description){
        super(eventCode.getMessage());
        this.code = eventCode.getCode();
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
