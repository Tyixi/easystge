package com.yixi.ucenter.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@ToString
@Data
public class User implements Serializable {
    /**
     * 主建id
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    /**
     * 昵称
     */
   // @TableField(value = "nick_name")
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
     * 密码
     */
    private String password;

    /**
     * 使用空间单位byte
     */
   // @TableField(value = "use_space")
    private Long useSpace;

    /**
     * 总空间
     */
    //@TableField(value = "total_space")
    private Long totalSpace;

    /**
     * 用户角色
     */
    //@TableField(value = "last_login_time")
    private Integer userRole;

    /**
     * 最后一次登录时间
     */
    //@TableField(value = "last_login_time")
    private Date lastLoginTime;

    /**
     * '逻辑删除 1（true）已删除， 0（false）未删除'
     */
   // @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}