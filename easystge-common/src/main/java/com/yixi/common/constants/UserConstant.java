package com.yixi.common.constants;

/**
 * 用户静态常量
 *
 * @author yixi
 * @date 2023/8/6
 * @apiNote
 */
public class UserConstant {

    /**
     * 用户云盘初始化空间
     */
    public static final Long USER_INIT_TOTAL_SPACE = 10 * 1024 * 1024L;

    /**
     * 用户登录信息在redis中的key
     */
    public static final String USER_LOGIN_INFO = "easystge:user:login:info:";

}
