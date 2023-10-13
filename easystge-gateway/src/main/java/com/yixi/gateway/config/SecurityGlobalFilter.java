package com.yixi.gateway.config;

import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;
import com.yixi.common.constants.SecurityConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
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
        System.out.println("gateway filter");
        ServerHttpRequest request = exchange.getRequest();
//        List<String> autoHeads = request.getHeaders().get(SecurityConstant.AUTHORIZATION_HEAD);
//        if (autoHeads==null || autoHeads.size() < 1){
//            throw new BusinessException(EventCode.NOT_LOGIN);
//        }
//        // 判断用户是否登录
//        try {
//            String jwtToken = autoHeads.get(0);
//            JwtUtils.getUserIdByJwtToken(jwtToken);
//        }catch (Exception e){
//            throw new BusinessException(EventCode.NOT_LOGIN);
//        }


        String path = request.getURI().getPath();
        //校验用户必须登录
        /**
         * oauth 接口都放行，其他的都要拦截查看权限
         * 1、获取token 查看用户是否已登录
         * 2、已登录用户查看是否有操作权限
         * 3、已登录且有权限才放行
         */
        if(antPathMatcher.match("/**/auth/**", path)) {
            System.out.println("必须登录");
            List<String> tokenList = request.getHeaders().get("token");
            if(null == tokenList) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
            } else {
//                Boolean isCheck = JwtUtils.checkToken(tokenList.get(0));
//                if(!isCheck) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
//                }
            }
        }
        //内部服务接口，不允许外部访问
        if(antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            return out(response);
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
