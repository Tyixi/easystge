package com.yixi.common.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 *
 * @author yixi
 * @date 2023/8/22
 * @apiNote
 */
@Data
public class UserInfo implements Serializable {
    /**
     * 主建id
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;


    /**
     * 使用空间单位byte
     */
    private Long useSpace;

    /**
     * 总空间
     */
    private Long totalSpace;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginTime;

    /**
     * '逻辑删除 1（true）已删除， 0（false）未删除'
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}