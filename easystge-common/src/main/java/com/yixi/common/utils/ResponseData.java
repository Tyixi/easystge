package com.yixi.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */

@Getter
@AllArgsConstructor
public class ResponseData<T> {
    private Boolean success;  // 是否请求成功
    private String message;   // 请求通知
    private String code;    // 业务状态码
    private T data; //数据

    public ResponseData(Boolean success, String message, String code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }
}
