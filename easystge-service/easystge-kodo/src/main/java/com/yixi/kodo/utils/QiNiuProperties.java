package com.yixi.kodo.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author yixi
 * @date 2023/8/24
 * @apiNote
 */
@Component
public class QiNiuProperties implements InitializingBean {
    // 读取配置文件内容
    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value("${qiniu.secretKey}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("${qiniu.path}")
    private String path;

    // 定义公开常量
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String BUCKET;
    public static String PATH;

    @Override
    public void afterPropertiesSet() throws Exception {
        ACCESS_KEY = accessKey;
        SECRET_KEY = secretKey;
        BUCKET = bucket;
        PATH = path;
    }
}
