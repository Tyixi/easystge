package com.yixi.common.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author helen
 * @since 2019/10/16
 */
public class JwtUtils {


    public static final long EXPIRE = 1000 * 60 * 60 * 24; //token过期时间
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO"; //密钥 做加密操作

    /**
     * 生成token字符串
     * @param id
     * @param nickname
     * @return
     */
    public static String getJwtToken(String id, String nickname){

        String JwtToken = Jwts.builder()
                //设置token的头信息
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //设置过期时间
                .setSubject("yixi-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                //设置token主体部分 , 存储用户信息
                .claim("id", id)
                .claim("nickname", nickname)

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
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if(!StringUtils.hasLength(jwtToken))
            return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }
}