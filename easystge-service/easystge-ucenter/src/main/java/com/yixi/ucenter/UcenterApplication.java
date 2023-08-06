package com.yixi.ucenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yixi
 * @date 2023/8/6
 * @apiNote
 */
@SpringBootApplication(scanBasePackages = {"com.yixi"})
public class UcenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UcenterApplication.class, args);
    }
}
