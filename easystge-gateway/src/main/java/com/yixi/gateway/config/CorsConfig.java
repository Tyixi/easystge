package com.yixi.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 处理跨域
 *
 * @author yixi
 * @since 2023-8-7
 */

@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        //跨域有关配置
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true); // 允许携带cookie进行跨域，不设置的话跨域请求会丢失相关的cookie信息

        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}