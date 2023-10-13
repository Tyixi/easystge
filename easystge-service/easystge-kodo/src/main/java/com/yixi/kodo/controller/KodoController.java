package com.yixi.kodo.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.kodo.model.vo.UploadFileVo;
import com.yixi.kodo.service.KodoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author yixi
 * @date 2023/8/23
 * @apiNote
 */
@RestController
@RequestMapping("/easystgekodo/filekodo")
public class KodoController {
    final private KodoService kodoService;

    public KodoController(KodoService kodoService){
        this.kodoService = kodoService;
    }


    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse uploadKodoFile(MultipartFile file){
        if (file == null) throw new BusinessException(EventCode.NULL_ERROR);
        // 获取上传文件
        // 返回上传到kodo的路径
        String url = kodoService.uploadFile(file);
        return ResultUtils.success(url);
    }

    /**
     * 分片上传
     * @param uploadFileVo
     * @return
     */
//    @PostMapping("/upload/chunk")
//    public BaseResponse uploadChunk(@RequestPart("uploadFileVo")  UploadFileVo uploadFileVo){
//        if (uploadFileVo == null) throw new BusinessException(EventCode.NULL_ERROR);
//        String url = kodoService.uploadChunk(uploadFileVo);
//        return ResultUtils.success(url);
//    }

    @PostMapping(path = "/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadChunk(@RequestParam Map<String, Object> info,
                                    @RequestPart(value = "uploadFileVo", required = false)  MultipartFile uploadFileVo){
        System.out.println("info is " + info.toString());
        System.out.println("multipartFile is "+uploadFileVo);
        kodoService.uploadChunk(info,uploadFileVo);
//        if (uploadFileVo == null) throw new BusinessException(EventCode.NULL_ERROR);
//        String url = kodoService.uploadChunk(uploadFileVo);
        return ResultUtils.success("url");
    }



}
