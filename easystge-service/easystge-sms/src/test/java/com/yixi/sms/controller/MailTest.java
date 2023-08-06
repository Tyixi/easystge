package com.yixi.sms.controller;

import com.yixi.common.exception.BusinessException;
import com.yixi.sms.service.SmsService;
//import org.junit.Test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */

@SpringBootTest
public class MailTest {

    @Resource
    private SmsService smsService;

    @Test
    public void sendMail() throws BusinessException {
//        MailUtil.send("1345286878@qq.com",
//                "测试邮箱发送",
//                "测试内容 <h1>这是邮件</h1>",
//                true );
        System.out.println(smsService);

        boolean b = smsService.sendMailVc("345543", "1345286878@qq.com");
        System.out.println(b);
    }
}
