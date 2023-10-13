package com.yixi.kodo.service;

import com.yixi.kodo.model.vo.UploadFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/24
 * @apiNote
 */
public interface KodoService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传文件分片
     */
    String uploadChunk(Map<String, Object> fileInfo, MultipartFile file);
}
