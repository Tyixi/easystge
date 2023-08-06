package com.yixi.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author yixi
 * @date 2023/8/5
 * @apiNote
 */
@SpringBootApplication
@ComponentScan("com.yixi")
public class SmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }
}
