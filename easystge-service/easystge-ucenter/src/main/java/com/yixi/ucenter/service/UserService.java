package com.yixi.ucenter.service;

import com.yixi.common.exception.BusinessException;
import com.yixi.ucenter.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.ucenter.model.vo.UserLoginVo;
import com.yixi.ucenter.model.vo.UserRegistVo;
import com.yixi.ucenter.model.vo.UserSpaceVo;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
* @author yixi
* @description 针对表【user】的数据库操作Service
* @createDate 2023-08-05 22:05:19
*/
public interface UserService extends IService<User> {

    /**
     * 登录
     * @param userLoginVo
     * @return
     */
    @Transactional
    Map login(UserLoginVo userLoginVo);

    /**
     * 用户注册
     * @param userVo
     */
    @Transactional
    String register(UserRegistVo userVo) throws BusinessException;

    /**
     * 用户找回密码
     * @param userVo
     * @return
     * @throws BusinessException
     */
    @Transactional
    String forgotPWD(UserRegistVo userVo) throws BusinessException;

    /**
     * 退出登录
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取用户空间
     * @param request
     * @return
     */
    UserSpaceVo findUserSpace(HttpServletRequest request);

    /**
     * 刷新用户空间
     * @param userId
     */
    void spaceRefresh(String userId);


}
