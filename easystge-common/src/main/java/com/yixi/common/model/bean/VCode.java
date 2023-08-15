package com.yixi.common.model.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 登录验证码配置信息
 * @author yixi
 * @date 2023/8/7
 * @apiNote
 */
@Data
@Component
public class VCode {
    /**
     * 验证码配置
     */
    private VCodeEnum vCodeEnum = VCodeEnum.ARITHMETIC;
    /**
     * 验证码有效期 分钟
     */
    private Long expiration = 2L;
    /**
     * 验证码内容长度
     */
    private int length = 2;
    /**
     * 验证码宽度
     */
    private int width = 111;
    /**
     * 验证码高度
     */
    private int height = 36;
    /**
     * 验证码字体
     */
    private String fontName;
    /**
     * 字体大小
     */
    private int fontSize = 25;

    public VCodeEnum getCodeType() {
        return vCodeEnum;
    }
}
