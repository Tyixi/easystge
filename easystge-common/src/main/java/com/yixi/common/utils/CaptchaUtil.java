package com.yixi.common.utils;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.model.bean.VCode;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Objects;

/**
 * 验证码
 * @author yixi
 * @date 2023/8/7
 * @apiNote
 */
@Data
@Component
public class CaptchaUtil {
    @Resource
    private VCode vCode;

    /**
     * 获取验证码生产类
     *
     * @return /
     */
    public Captcha getCaptcha() {

        return switchCaptcha(vCode);
    }

    /**
     * 依据配置信息生产验证码
     *
     * @param vCode 验证码配置信息
     * @return /
     */
    private Captcha switchCaptcha(VCode vCode) {
        Captcha captcha;
//        switch (vCode.getCodeType()) {
//            case ARITHMETIC:
                // 算术类型 https://gitee.com/whvse/EasyCaptcha
                captcha = new FixedArithmeticCaptcha(vCode.getWidth(), vCode.getHeight());
                // 几位数运算，默认是两位
                captcha.setLen(vCode.getLength());
//                break;
//            case CHINESE:
//                captcha = new ChineseCaptcha(vCode.getWidth(), vCode.getHeight());
//                captcha.setLen(vCode.getLength());
//                break;
//            case CHINESE_GIF:
//                captcha = new ChineseGifCaptcha(vCode.getWidth(), vCode.getHeight());
//                captcha.setLen(vCode.getLength());
//                break;
//            case GIF:
//                captcha = new GifCaptcha(vCode.getWidth(), vCode.getHeight());
//                captcha.setLen(vCode.getLength());
//                break;
//            case SPEC:
//                captcha = new SpecCaptcha(vCode.getWidth(), vCode.getHeight());
//                captcha.setLen(vCode.getLength());
//                break;
//            default:
//                throw new BusinessException(EventCode.SYSTEM_ERROR,"验证码配置信息错误！正确配置查看 VCodeEnum ");
//        }
        if(StringUtils.hasLength(vCode.getFontName())){
            captcha.setFont(new Font(vCode.getFontName(), Font.PLAIN, vCode.getFontSize()));
        }
        return captcha;
    }

    static class FixedArithmeticCaptcha extends ArithmeticCaptcha {
        public FixedArithmeticCaptcha(int width, int height) {
            super(width, height);
        }

        @Override
        protected char[] alphas() {
            // 生成随机数字和运算符
            int n1 = num(1, 10), n2 = num(1, 10);
            int opt = num(3);

            // 计算结果
            int res = new int[]{n1 + n2, n1 - n2, n1 * n2}[opt];
            // 转换为字符运算符
            char optChar = "+-x".charAt(opt);

            this.setArithmeticString(String.format("%s%c%s=?", n1, optChar, n2));
            this.chars = String.valueOf(res);

            return chars.toCharArray();
        }
    }
}
