package com.yixi.file.client;

import com.yixi.common.utils.BaseResponse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/24
 * @apiNote
 */
@Mapper
@FeignClient("service-kodo")
public interface KodoClient {

    /**
     * 远程调用kodo服务
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/easystgekodo/filekodo/upload")
    public BaseResponse uploadFile(MultipartFile file);


    /**
     * 远程调用kodo服务
     * 分片上传
     * @param
     * @return
     */
//    @PostMapping(value = "/easystgekodo/filekodo/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public BaseResponse uploadChunk(@RequestPart("uploadFileVo") UploadFileVo uploadFileVo);
    @PostMapping(value = "/easystgekodo/filekodo/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadChunk(@SpringQueryMap Map<String, Object> info,
                                    @RequestPart(value = "uploadFileVo", required = false)  MultipartFile uploadFileVo);
}
