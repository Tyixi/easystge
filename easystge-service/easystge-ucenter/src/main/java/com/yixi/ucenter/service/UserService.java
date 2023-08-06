package com.yixi.ucenter.service;

import com.yixi.common.exception.BusinessException;
import com.yixi.ucenter.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.ucenter.model.vo.UserRegistVo;

/**
* @author yixi
* @description 针对表【user】的数据库操作Service
* @createDate 2023-08-05 22:05:19
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userVo
     */
    String register(UserRegistVo userVo) throws BusinessException;
}
