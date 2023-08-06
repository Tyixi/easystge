package com.yixi.ucenter.controller;

import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.ucenter.model.vo.UserRegistVo;
import com.yixi.ucenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author yixi
 * @date 2023/8/6
 * @apiNote
 */
@RestController
@RequestMapping("/userIndex")
@Slf4j
public class LoginController {

    final private UserService userService;

    public LoginController(UserService userService){
        this.userService = userService;
    }


    @PostMapping("/register")
    public BaseResponse regist(@RequestBody UserRegistVo userVo) throws BusinessException {
        //判断请求参数是否为空
        if (userVo == null){
            return ResultUtils.error(EventCode.PARAMS_ERROR);
        }
        System.out.println(userVo);
        //进行注册
        String result = userService.register(userVo);

        return ResultUtils.success(result);
    }


}
