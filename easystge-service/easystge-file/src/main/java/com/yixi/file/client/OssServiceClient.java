package com.yixi.file.client;

import com.yixi.common.utils.BaseResponse;
import feign.Response;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 远程调用oss服务
 *
 * @author yixi
 * @date 2023/8/28
 * @apiNote
 */
@Mapper
@FeignClient("service-oss")
public interface OssServiceClient {

    /**
     *
     * 阿里云oss获取分片唯一ID
     * @param taskKey
     * @return
     */
    @GetMapping("/ossservice/ossChunkId")
    public BaseResponse getOssChunkId(@RequestParam("taskKey") String taskKey);


    /**
     *
     * 上传文件分片
     * @param info
     * @param file
     * @return
     */
    @PostMapping(value = "/ossservice/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadChunk(@RequestParam Map<String, Object> info,
                                    @RequestPart(value = "file", required = false) MultipartFile file);


    /**
     * 获取文件流
     * @param url
     * @return
     */
    @GetMapping("/ossservice/file/inputStream")
    public Response getFileInputStream(@RequestParam("url") String url);

}
