package com.yixi.ucenter.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录表单信息
 *
 * @author yixi
 * @date 2023/8/8
 * @apiNote
 */
@Data
public class UserLoginVo {
    /**
     * 用户邮箱号码
     */
    @ApiModelProperty(value = "用户邮箱号码")
    private String email;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    private String password;


    /**
     * 验证码
     */
    @ApiModelProperty(value = "图形验证码")
    private String verifyCode;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "图形验证码Key")
    private String vcKey;
}
