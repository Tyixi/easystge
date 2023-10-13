package com.yixi.file.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 分享文件信息
 * @TableName file_share
 */
@TableName(value ="file_share")
@Data
public class FileShare implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private String shareId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 有效期类型  1: 一天  2：七天  3：永久有效
     */
    private Integer validType;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 分享文件被浏览次数
     */
    private Integer showCount;

    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}