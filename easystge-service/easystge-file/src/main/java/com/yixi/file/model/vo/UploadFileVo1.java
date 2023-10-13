package com.yixi.file.model.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author yixi
 * @date 2023/8/24
 * @apiNote
 */
@Data
public class UploadFileVo1 implements Serializable {

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * md5
     */
    private String md5;

    /**
     * 当前分片索引
     */
    private Integer chunkIndex;

    /**
     * 总分片数
     */
    private Integer chunks;


    private static final long serialVersionUID = 1L;

}
