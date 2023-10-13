package com.yixi.common;

import com.yixi.common.utils.JwtUtils;

/**
 * @author yixi
 * @date 2023/8/10
 * @apiNote
 */

public class Test1 {
    public static void main(String[] args) {

        String token = JwtUtils.getJwtToken("1689901755352621057", "1345286878@qq.com");
        System.out.println("token=" + token);
        System.out.println("id");
        System.out.println(JwtUtils.getUserIdByJwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5aXhpLXVzZXIiLCJpYXQiOjE2OTE4MDc4OTIsImV4cCI6MTY5MTgwNzk1MiwiaWQiOiIxMiIsImVtYWlsIjoiaW8ifQ.nZjblxlYT7LdZzq7Cb6ZL7MDJNtP_fNf_73MwizxHlA"));
        //System.out.println("claims=" + JwtUtils.getClaim("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5aXhpLXVzZXIiLCJpYXQiOjE2OTE2NzE1NTIsImV4cCI6MTY5MTY3MTYxMiwiaWQiOiIxMiIsInVzZXJuYW1lIjoiaW8ifQ.0pnV-Bf4FnKeasbi6YhvHqn0rMzkOr_EhBWodjM2EWA"));
        //System.out.println("失效时间= " + JwtUtils.getExpiration("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5aXhpLXVzZXIiLCJpYXQiOjE2OTE2NzE1NTIsImV4cCI6MTY5MTY3MTYxMiwiaWQiOiIxMiIsInVzZXJuYW1lIjoiaW8ifQ.0pnV-Bf4FnKeasbi6YhvHqn0rMzkOr_EhBWodjM2EWA"));
//        try {
//            JwtUtils.getUserIdByJwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5aXhpLXVzZXIiLCJpYXQiOjE2OTE2NzI0MjEsImV4cCI6MTY5MTY3MjQ4MSwiaWQiOiIxMiIsInVzZXJuYW1lIjoiaW8ifQ.J6wKkZjHY5jjwPCJh-gx_uUPsXRfMawufLDineiVUo8");
//        }catch (Exception e){
//            System.out.println("捕获异常");
//        }

    }
}
