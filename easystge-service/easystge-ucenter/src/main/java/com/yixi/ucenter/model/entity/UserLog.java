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
 * 用户日志
 * @TableName user_log
 */
@TableName(value ="user_log")
@ToString
@Data
public class UserLog implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 描述
     */
    private String logDesc;

    /**
     * 时间
     */
    private Date logTime;

    /**
     * 事件
     */
    private String logEvent;

    /**
     * 用户id
     */
    private String userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}