package com.yixi.sms.controller;

import com.yixi.common.constants.MQConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.model.bean.EmailVerify;
import com.yixi.common.utils.*;
import com.yixi.sms.service.SmsService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final SmsService smsService;
    private final RabbitTemplate rabbitTemplate;

    public SmsController(SmsService smsService,RabbitTemplate rabbitTemplate){
        this.smsService = smsService;
        this.rabbitTemplate = rabbitTemplate;
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

        //异步调用service发送短信方法
        EmailVerify emailVerify = new EmailVerify(email,code);
        rabbitTemplate.convertAndSend(MQConstant.PARK_ROUTE_EXCHANGE,MQConstant.USER_REG_EMAIL,emailVerify);

        return ResultUtils.success(true);
    }

}
