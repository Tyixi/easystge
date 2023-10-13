package com.yixi.file.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName e_file
 */
@TableName(value ="e_file")
@Data
public class EFile implements Serializable {
    /**
     * 文件主键id
     */
    @TableId
    private String fileId;

    /**
     * 所属用户id
     */
    private String userId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件md5值
     */
    private String fileMd5;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * 文件大小 单位byte
     */
    private Long fileSize;

    /**
     * 文件封面
     */
    private String fileCover;

    /**
     * 文件路径 
     */
    private String filePath;

    /**
     * 0:文件 1:目录
     */
    private Integer folderType;

    /**
     * 1:视频 2:音频 3:图片 4文档 5其他  文件分类
     */
    private Integer fileCategory;

    /**
     * 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:zip 9:其他
     */
    private Integer fileType;

    /**
     * 0:转码中 1转码失败 2转码成功 状态
     */
    private Integer status;

    /**
     * 进入回收站时间
     */
    private Date recoveryTime;

    /**
     * 1: 正常 2: 回收站 3: 逻辑删除
     */
    private Integer delFlag;

    /**
     * 1:回收站  0:正常
     */
    private Integer isDeleted;

    /**
     * 文件创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="Asia/Shanghai")
    private Date updateTime;

    @TableField(exist = false)
    private List<EFile> childList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}