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
 * 文件回收站
 * @TableName file_recycle
 */
@TableName(value ="file_recycle")
@Data
public class FileRecycle implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String recycleId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private LocalDateTime endTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}