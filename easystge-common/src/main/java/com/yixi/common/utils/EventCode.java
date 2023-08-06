package com.yixi.common.utils;

import lombok.Data;
/**
 * 错误码列表
 * 40
 *      000:请求参数错误
 *      001:请求数据为空
 *      002:短信验证码频率太高
 *      100:未登录
 *      101:无权限
 *41    用户异常
 *      001:邮箱已被注册
 * 50
 *      000:系统内部异常
 */

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */

public enum EventCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000, "请求参数错误",""),
    NULL_ERROR(40001, "请求数据为空",""),
    SMS_CODE_EXCEPTION(40002,"短信验证码频率太高，请稍后再试",""),
    NOT_LOGIN(40100, "未登录",""),
    NO_AUTH(40101, "无权限",""),
    USER_EXIST_EXCEPTION(41001,"邮箱已被注册",""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    private final int code;
    private final String message;
    private final String description;

    EventCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
