package com.yixi.common.utils;

import com.yixi.common.exception.BusinessException;
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author yixi
 */
public class JwtUtils {


    public static final long EXPIRE = 60 * 1000 * 2; //token过期时间
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO"; //密钥 做加密操作

    /**
     * 生成token字符串
     * @param id
     * @param email
     * @return
     */
    public static String getJwtToken(String id, String email){

        String JwtToken = Jwts.builder()
                //设置过期时间
                .setSubject("yixi-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                //设置token主体部分 , 存储用户信息
                .claim("id", id)
                .claim("email", email)
                //签名
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();

        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if(!StringUtils.hasLength(jwtToken)) return false;
        try {
            //根据密钥验证token是否有效
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if(!StringUtils.hasLength(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取用户id
     * @param request
     * @return
     */
    public static String getUserIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if(!StringUtils.hasLength(jwtToken))
            return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }


    /**
     * 根据token获取用户id
     * @param token
     * @return
     */
    public static String getUserIdByJwtToken(String token) {
        if(!StringUtils.hasLength(token))
            return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }

    /**
     * 获取payload 部分内容（即要传的信息）
     * 使用方法：如获取userId：getClaim(token).get("userId");
     *
     * @param token
     * @return
     */
    public static Claims getClaim(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(APP_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }


    /**
     * 获取jwt失效时间
     */
    public static Date getExpiration(String token) {
        return getClaim(token).getExpiration();
    }



}