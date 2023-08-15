package com.yixi.ucenter.controller;

import cn.hutool.core.util.IdUtil;
import com.wf.captcha.base.Captcha;
import com.yixi.common.constants.MConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.model.bean.VCodeEnum;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.CaptchaUtil;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.ucenter.model.vo.UserLoginVo;
import com.yixi.ucenter.model.vo.UserRegistVo;
import com.yixi.ucenter.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yixi
 * @date 2023/8/6
 * @apiNote
 */
@RestController
@RequestMapping("/easystgeucenter/open")
@Slf4j
public class LoginController {

    final private UserService userService;
    final private RedisTemplate<String, Object> redisTemplate;
    final private CaptchaUtil captchaUtil;


    public LoginController(UserService userService,RedisTemplate<String, Object> redisTemplate,CaptchaUtil captchaUtil){
        this.userService = userService;
        this.redisTemplate = redisTemplate;
        this.captchaUtil = captchaUtil;

    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public BaseResponse login(@RequestBody UserLoginVo loginVo){
        //判断请求参数是否为空
        if (loginVo == null){
            return ResultUtils.error(EventCode.PARAMS_ERROR);
        }
        // 进行登录
        Map result = userService.login(loginVo);

        return ResultUtils.success(result);
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public BaseResponse regist(@RequestBody UserRegistVo userVo) throws BusinessException {
        //判断请求参数是否为空
        if (userVo == null){
            return ResultUtils.error(EventCode.PARAMS_ERROR);
        }
        //进行注册
        String result = userService.register(userVo);

        return ResultUtils.success(result);
    }


    @ApiOperation("获取邮箱验证码")
    @GetMapping(value = "/code")
    public BaseResponse getCode() {
        // 获取运算的结果
        Captcha captcha = captchaUtil.getCaptcha();
        String vcKey = MConstant.CAPTCHA_CODE_KEY + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == VCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisTemplate.opsForValue().set(vcKey, captchaValue,captchaUtil.getVCode().getExpiration(),TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("vcKey", vcKey);
        }};
        return ResultUtils.success(imgResult);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public BaseResponse logout(HttpServletRequest request){
        if (request == null) {
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

}
