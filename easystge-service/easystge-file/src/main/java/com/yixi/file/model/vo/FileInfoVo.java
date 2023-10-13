package com.yixi.file.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author yixi
 * @date 2023/8/15
 * @apiNote
 */
@Data
public class FileInfoVo implements Serializable {
    /**
     * 文件主键id
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;


    /**
     * 文件封面
     */
    private String fileCover;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * 文件大小 单位byte
     */
    private Long fileSize;


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
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+0")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
