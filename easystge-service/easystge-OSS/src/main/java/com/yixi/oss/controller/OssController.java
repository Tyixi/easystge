package com.yixi.oss.controller;

import cn.hutool.json.JSONUtil;
import com.aliyun.oss.model.PartETag;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.oss.model.dto.UploadFileDto;
import com.yixi.oss.model.dto.UploadFileVo;
import com.yixi.oss.utils.OSSUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yixi
 * @date 2023/8/28
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/ossservice")
public class OssController {
    private final RedisTemplate<String,Object> redisTemplate;
    private final OSSUtil ossUtil;



    public OssController(OSSUtil ossUtil,RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.ossUtil = ossUtil;
    }


    @ApiOperation("阿里云oss获取分片唯一ID")
    @GetMapping("/ossChunkId")
    public BaseResponse getOssChunkId(@RequestParam("taskKey") String taskKey){
        log.info("获取分片唯一ID");
        log.info("taskKey is {}", taskKey);
        // 请求阿里云oss获取分片唯一ID
        String ossChunkId = ossUtil.getUploadId(taskKey);
        log.info("生成的oss分片唯一id：{}",ossChunkId);
//        result.setOssChunkId(ossChunkId);
//        result.setMinSliceSize(minChunkSize+"k");
//        // 缓存到redis
//        redisTemplate.opsForValue().set(ossChunkId,
//                JSONUtil.toJsonStr(result));
        return ResultUtils.success(ossChunkId);
    }

    @ApiOperation("分片上传")
    @PostMapping(value = "/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadChunk(@RequestParam Map<String, Object> info,
                                    @RequestPart(value = "file", required = false)  MultipartFile file){
        log.info("文件分片上传");
        if (info == null || file == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        String url = null;      // 保存文件最终路径
        UploadFileDto uploadFileDto = new UploadFileDto();
        try {
            uploadFileDto.setTaskId((String) info.get("taskId"));
            uploadFileDto.setFileName((String) info.get("fileName"));
            uploadFileDto.setChunkIndex(Integer.valueOf((String) info.get("chunkIndex")));
            uploadFileDto.setChunks(Integer.valueOf((String) info.get("chunks")));
            uploadFileDto.setOssChunkId((String) info.get("ossChunkId"));
            uploadFileDto.setFileStream(file.getInputStream());
            uploadFileDto.setFileMd5((String) info.get("fileMd5"));
            uploadFileDto.setFileSize(file.getSize());
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR,e.getMessage());
        }

        // 从redis中获取PartETags, 这是分片合成文件中需要的依据，合并文件返回最终地址
        UploadFileDto redisParam = new UploadFileDto();
        String redisParamJson = (String)(redisTemplate.opsForValue().get(uploadFileDto.getOssChunkId()));

        if (redisParam != null){
            redisParam = JSONUtil.toBean(redisParamJson, UploadFileDto.class);
            uploadFileDto.setPartETagMap(redisParam.getPartETagMap());
        }

        int chunkIndex = uploadFileDto.getChunkIndex();     // 当前分片
        int fileChunks = uploadFileDto.getChunks();         // 总分片数
        String ossChunkId = uploadFileDto.getOssChunkId();  // 分片唯一标识

        // 字节流
        InputStream inputStream = uploadFileDto.getFileStream();
        Map<Integer, PartETag> partETagMap = uploadFileDto.getPartETagMap();
        //分片上传
        try {
            PartETag partETag = ossUtil.partUploadFile(uploadFileDto.getTaskId()+uploadFileDto.getFileName(),
                    inputStream,
                    ossChunkId,
                    uploadFileDto.getFileMd5(),
                    chunkIndex,
                    uploadFileDto.getFileSize());
            partETagMap.put(chunkIndex,partETag);
            // 分片编号等于总片数的时候合并文件，如果符合条件则合并文件，否则继续等待
            if (fileChunks == chunkIndex){
                log.info("合并文件");
                // 合并文件
                System.out.println("uploadFileDto.getTaskId()+uploadFileDto.getFileName() is "+uploadFileDto.getTaskId()+uploadFileDto.getFileName());
                url = ossUtil.completerPartUploadFile(uploadFileDto.getTaskId()+uploadFileDto.getFileName(), ossChunkId, new ArrayList<>(partETagMap.values()));
                log.info("文件最终地址为：{}",url);
                // oss 地址返回后保存并清除redis中的缓存
                uploadFileDto.setFileUrl(url);
                redisTemplate.delete(ossChunkId);
            }else{
                // partETags必须是所有分片的所以必须存入redis
               // redisTemplate.opsForValue().set(ossChunkId, uploadFileDto);
                uploadFileDto.setFileStream(null);
                redisTemplate.opsForValue().set(ossChunkId, JSONUtil.toJsonStr(uploadFileDto));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(url);
    }


    @ApiOperation("获取文件流")
    @GetMapping("/file/inputStream")
    public void getFileInputStream(@RequestParam("url") String url, HttpServletResponse response){
        log.info("url is "+url);
        if (!StringUtils.hasLength(url)){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(url, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/octet-stream; charset=UTF-8");  //设置请求参数
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            inputStream = ossUtil.getInputStream(url);

            byte[] bytes = new byte[1024];
            int index;
            while((index = inputStream.read(bytes))!= -1){
                outputStream.write(bytes, 0, index);
                outputStream.flush();
            }

        }catch (Exception e){
            response.setStatus(0);
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }
    }

}
