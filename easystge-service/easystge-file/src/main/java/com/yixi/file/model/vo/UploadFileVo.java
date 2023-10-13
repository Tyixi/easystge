package com.yixi.file.model.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/28
 * @apiNote
 */
@Data
public class UploadFileVo {
    /**
     * 初始化任务id
     */
    private String taskId;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 文件总大小（不是分片）
     */
    private Long fileTotalSize;

    /**
     * 文件流
     */
    private InputStream fileStream;

    /**
     * 上传文件类型
     */
    private String fileType;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * 文件总片数
     */
    private Integer chunks;

    /**
     * 分片编号
     */
    private Integer chunkIndex;

    /**
     * md5
     */
    private String fileMd5;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * oss初始化分片id
     */
    private String ossChunkId;

    /**
     * 每次上传分片的大小 (最后一片可能小于该值)
     */
    private Integer minSliceSize;


}
