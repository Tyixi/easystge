package com.yixi.ucenter.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
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
    @TableField(value = "nick_name")
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
//    @TableField(value = "use_space")
    private Long useSpace;

    /**
     * 总空间
     */
//    @TableField(value = "total_space")
    private Long totalSpace;

    /**
     * 最后一次登录时间
     */
    @TableField(value = "last_login_time")
    private Date lastLoginTime;

    /**
     * '逻辑删除 1（true）已删除， 0（false）未删除'
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        User other = (User) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getNickName() == null ? other.getNickName() == null : this.getNickName().equals(other.getNickName()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getUseSpace() == null ? other.getUseSpace() == null : this.getUseSpace().equals(other.getUseSpace()))
            && (this.getTotalSpace() == null ? other.getTotalSpace() == null : this.getTotalSpace().equals(other.getTotalSpace()))
            && (this.getLastLoginTime() == null ? other.getLastLoginTime() == null : this.getLastLoginTime().equals(other.getLastLoginTime()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getNickName() == null) ? 0 : getNickName().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getUseSpace() == null) ? 0 : getUseSpace().hashCode());
        result = prime * result + ((getTotalSpace() == null) ? 0 : getTotalSpace().hashCode());
        result = prime * result + ((getLastLoginTime() == null) ? 0 : getLastLoginTime().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", nickName=").append(nickName);
        sb.append(", email=").append(email);
        sb.append(", avatar=").append(avatar);
        sb.append(", password=").append(password);
        sb.append(", useSpace=").append(useSpace);
        sb.append(", totalSpace=").append(totalSpace);
        sb.append(", lastLoginTime=").append(lastLoginTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}