package com.yixi.common.constants;

/**
 * 邮箱静态常量
 *
 * @author yixi
 * @date 2023/8/6
 * @apiNote
 */
public class EmailConstant {

    /**
     * 用户注册 邮箱验证码在redis中的key
     */
    public static final String EMAIL_VC_KEY = "easystge:registerVC:";

    /**
     * 忘记密码 邮箱验证码在redis中的key
     */
    public static final String EMAIL_VC_FORGOT_KEY = "easystge:forgotVC:";

    /**
     * 邮箱验证码有效时间 单位分钟
     */
    public static final int EMAIL_VC_VALID_TIME = 15;

}
