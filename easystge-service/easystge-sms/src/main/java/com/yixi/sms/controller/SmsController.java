package com.yixi.sms.controller;

import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.*;
import com.yixi.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */


@RestController
@RequestMapping("/easystgesms")
public class SmsController {

    final private SmsService smsService;

    public SmsController(SmsService smsService){
        this.smsService = smsService;
    }


    /**
     * 邮箱注册-发送邮箱验证码
     * @param email
     * @return
     * @throws Exception
     */
    @GetMapping("/open/register/vc/{email}")
    public BaseResponse sendSmsRegister(@PathVariable String email) throws Exception{
        if (!StringUtils.hasLength(email)) throw new BusinessException(EventCode.NULL_ERROR);

        //生成随机值
        String code = RandomUtil.getSixBitRandom();
        //调用service发送短信方法
        boolean isSend = smsService.sendMailVc(code,email);
        return ResultUtils.success(isSend);
    }

}
