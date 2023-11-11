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
 *      102:登录失败
 *      103 TOKEN 无效
 *41    用户异常
 *      001:邮箱已被注册
 *      002:账户异常
 *      003:空间不足
 *      004:用户不存在
 *
 *42   文件
 *      001:目录下文件名重复
 *      002:文件不存在
 *      003:文件或文件夹已经存在
 *      101:分享文件链接失效
 *      102:提取码错误
 *
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
    SMS_CODE_EXCEPTION(40002,"短信验证码频率太高",""),
    NOT_LOGIN(40100, "未登录",""),
    NO_AUTH(40101, "无权限",""),
    LOGIN_FAIL(40102, "登录失败",""),
    INVALID_TOKEN(40103, "TOKEN无效",""),
    USER_EXIST_EXCEPTION(41001,"邮箱已被注册",""),
    ACCOUNT_EXCEPTION(41002,"账户异常",""),
    ACCOUNT_SPACE_INSUFFICIENT(41003,"空间不足",""),
    USER_NOT_EXIST_EXCEPTION(41004,"用户不存在",""),
    FILE_NAME_REPEAT(42001,"目录下文件名重复",""),
    FILE_NOT_EXIST(42002,"文件不存在",""),
    FILE_ALREADY_EXIST(42003,"文件或文件夹已经存在",""),
    FILE_SHARE_INVALID(42101,"分享文件链接失效",""),
    SHARE_CODE_ERROR(42102,"提取码错误",""),
    SHARE_VERIFY_INVALID(42103,"分享文件验证失效",""),
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
