package com.yixi.kodo.service;

import java.io.InputStream;

/**
 * 上传服务
 *
 * @author yixi
 * @date 2023/8/23
 * @apiNote
 */
public interface UploadService {

    /**
     * 文件上传
     * @param inputStream
     * @param fileName
     * @return
     */
    String upload(InputStream inputStream,String fileName);

}
