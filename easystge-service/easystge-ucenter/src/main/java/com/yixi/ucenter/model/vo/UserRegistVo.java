package com.yixi.ucenter.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户注册表单信息
 * @author yixi
 * @date 2023/8/6
 * @apiNote
 */
@Data
public class UserRegistVo {

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
     *  邮箱验证码
     */
    @ApiModelProperty(value = "邮箱验证码")
    private String emailVC;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String verifyCode;

}
