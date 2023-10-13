package com.yixi.kodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author yixi
 * @date 2023/8/24
 * @apiNote
 */
@EnableFeignClients
@EnableSwagger2
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class KodoApplication {
    public static void main(String[] args) {
        SpringApplication.run(KodoApplication.class,args);
    }
}
