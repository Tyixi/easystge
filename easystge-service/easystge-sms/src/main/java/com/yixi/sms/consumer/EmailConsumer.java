package com.yixi.sms.consumer;

import com.yixi.common.constants.MQConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.model.bean.EmailVerify;
import com.yixi.common.utils.EventCode;
import com.yixi.sms.service.SmsService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author yixi
 * @date 2023/10/13
 * @apiNote
 */
@Component
public class EmailConsumer {

    private final SmsService smsService;

    public EmailConsumer(SmsService smsService){
        this.smsService = smsService;
    }

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,   // 创建临时队列
            exchange = @Exchange(value = MQConstant.PARK_ROUTE_EXCHANGE, type = MQConstant.DIRECT),
            key = {MQConstant.USER_REG_EMAIL})
    })
    public void sendEmail(EmailVerify emailVerify){
        System.out.println("注册用户邮件发送");
        try {
            smsService.sendMailVc(emailVerify.getCode(), emailVerify.getEmail());
        }catch (BusinessException businessException){
            businessException.printStackTrace();
        }


    }


    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,   // 创建临时队列
                    exchange = @Exchange(value = MQConstant.PARK_ROUTE_EXCHANGE, type = MQConstant.DIRECT),
                    key = {MQConstant.USER_FORGOT_PWD_EMAIL})
    })
    public void sendForgotPwdEmail(EmailVerify emailVerify){
        System.out.println("忘记密码邮件发送");
        try {
            smsService.sendMailForgotVc(emailVerify.getCode(), emailVerify.getEmail());
        }catch (BusinessException businessException){
            businessException.printStackTrace();
        }

    }
}
