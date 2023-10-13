package com.yixi.ucenter.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.ucenter.model.entity.User;
import com.yixi.ucenter.model.vo.UserSpaceVo;
import com.yixi.ucenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yixi
 * @date 2023/8/22
 * @apiNote
 */
@RestController
@RequestMapping("/easystgeucenter/user")
@Slf4j
public class UserController {
    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation("根据id查询单条数据")
    @GetMapping("/get/{id}")
    public UserInfo getUserById(@PathVariable("id") String id) {
        if (!StringUtils.hasLength(id)) throw new BusinessException(EventCode.NULL_ERROR);
        UserInfo userInfo = new UserInfo();
        User user = userService.getById(id);
        BeanUtil.copyProperties(user, userInfo);
        return userInfo;
    }

    @ApiOperation("获取用户空间")
    @GetMapping("/space")
    public BaseResponse getSpace(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户空间
        UserSpaceVo userSpace = userService.findUserSpace(request);

        return ResultUtils.success(userSpace);
    }


//
//    // 更新用户空间
    @PostMapping("/update")
    public BaseResponse updateUser(@RequestBody User user) {
        log.info("更新用户");
        if (user == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        boolean update = userService.updateById(user);
        System.out.println("更新完成");
        return ResultUtils.success(update);
    }

    // 刷新用户空间
    @PutMapping("/space/refresh")
    public BaseResponse spaceRefresh(String userId){
        log.info("刷新用户空间");

        return ResultUtils.success("");
    }
}

