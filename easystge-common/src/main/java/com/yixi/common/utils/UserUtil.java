package com.yixi.common.utils;

import com.yixi.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yixi
 * @date 2023/11/7
 * @apiNote
 */
public class UserUtil {
    /**
     * 根据request中的token获取userId
     * @param request
     * @return
     */
    public static String getUserIdByRequest(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        return  userId;
    }
}
