package com.yixi.oss.model.dto;

import com.aliyun.oss.model.PartETag;
import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/28
 * @apiNote
 */
@Data
public class UploadFileDto implements Serializable {
    /**
     * 初始化任务id
     */
    private String taskId;

    /**
     * 文件流
     */
    private InputStream fileStream;

    /**
     * 上传文件类型
     */
    private String fileType;

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
     * 文件大小
     */
    private Long fileSize;


    /**
     * 文件名称
     */
    private String fileName;

    /**
     * oss初始化分片id
     */
    private String ossChunkId;

    /**
     * 分片大小
     */
    private Integer minSliceSize;

    Map<Integer, PartETag> partETagMap = new HashMap<>(16);

    /**
     * 最终文件路径
     */
    private String fileUrl;

    private static final long serialVersionUID = 1L;

}
