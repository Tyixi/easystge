package com.yixi.gateway.config;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;
import com.yixi.common.constants.SecurityConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


/**
 * @author yixi
 * @date 2023/8/10
 * @apiNote
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityGlobalFilter implements GlobalFilter, Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (!(antPathMatcher.match("/**/open/**", path) || antPathMatcher.match("/**/exter/**", path))){    // 需要进行拦截验证身份
            System.out.println(request.getHeaders());
            List<String> tokenList = request.getHeaders().get(SecurityConstant.AUTHORIZATION_HEAD);
            // 获取用户身份令牌
            String jwtToken = null;
            boolean isLogin = false;
            try {
                jwtToken = tokenList.get(0).replace(SecurityConstant.TOKEN_PREFIX, ""); // 去除前缀
                // 检测token
                isLogin = JwtUtils.checkToken(jwtToken);
                // 获取token失效时间
                Date expirationTime = JwtUtils.getExpiration(jwtToken);
                Date currentTime = new Date();
                // token有效时间如果小于10分钟，则给用户重新设置token
                long between = DateUtil.between(currentTime, expirationTime, DateUnit.MINUTE);

                if (between < 10){
                    // 获取 用户id 和 用户邮箱
                    Claims claim = JwtUtils.getClaim(jwtToken);
                    String userId = (String)claim.get("id");
                    String email = (String)claim.get("email");
                    // 生成新的 token
                    String new_jwtToken = JwtUtils.getJwtToken(userId, email);
                    // 将生成的token保存到response中
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().set("Authorization",new_jwtToken);
                }
            }catch (Exception e){
                return out(exchange.getResponse());
            }
            if (!isLogin){  // 没有登录
                return out(exchange.getResponse());
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> out(ServerHttpResponse response) {
        JsonObject message = new JsonObject();
        message.addProperty("success", false);
        message.addProperty("code", 28004);
        message.addProperty("data", "鉴权失败");
        byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
