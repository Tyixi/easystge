package com.yixi.common.constants;

/**
 * 静态常量
 *
 * @author yixi
 * @date 2023/8/7
 * @apiNote
 */
public class MConstant {
    /**
     * 验证码在redis中的key
     */
    public static final String CAPTCHA_CODE_KEY = "easystge:vc:";


    /**
     * 分享文件信息存储在redis中的key
     */
    public static final String REDIS_SHARE_KEY = "easystge:share:key:";

    public static final String REDIS_DOWNLOAD_KEY = "easystge:download:";

    /**
     * 分享文件验证保存时间  单位：分钟
     */
    public static final Integer REDIS_SHARE_CODE_SAVE_TIME = 2;


}
