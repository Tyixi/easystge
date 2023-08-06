package com.yixi.sms.service.impl;

import cn.hutool.extra.mail.MailUtil;
import com.yixi.common.constants.EmailConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResponseData;
import com.yixi.sms.service.SmsService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */
@Service
public class SmsServiceImpl implements SmsService{

    final private StringRedisTemplate redisTemplate;

    public SmsServiceImpl(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    /**
     * 发送邮箱短信方法
     * @param code
     * @param email
     * @return
     */
    @Override
    public boolean sendMailVc(String code, String email) throws BusinessException {
        //判断邮箱号码是否为空
        if (!StringUtils.hasLength(email)){
            throw new BusinessException(EventCode.NULL_ERROR,"邮箱号码为空");
        }

        String vcKey = EmailConstant.EMAIL_VC_KEY +email;    // redis保存验证码的 key

        //  防止同一个email在60秒内再次发送验证码
        //  获取redis保存的code值
        String redisCode = redisTemplate.opsForValue().get(vcKey);
        if (StringUtils.hasLength(redisCode)){
            // 对code进行分割 获取code保存时的时间
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000){
                //60秒内不能再发
                throw new BusinessException(EventCode.SMS_CODE_EXCEPTION,EventCode.SMS_CODE_EXCEPTION.getMessage());
            }
        }






        // 发送验证码到邮箱
        String content = "<p>您好，您的邮箱验证码是：<b style='font-size:20px;color:blue;'>"+code+"</b>，"+EmailConstant.EMAIL_VC_VALID_TIME+"分钟有效</p>";
        code = code+"_"+System.currentTimeMillis();     //给验证码拼接上当前系统时间
        try {
            MailUtil.send(email,"注册邮箱账号验证码",content,true);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR, "验证码发送失败");
        }

        //将发送成功的验证码保存到redis中并设置有效时间
        try {
            redisTemplate.opsForValue().set(vcKey, code, EmailConstant.EMAIL_VC_VALID_TIME, TimeUnit.MINUTES);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR, "验证码存储失败");
        }

        return true;
    }
}
