package com.yixi.file.controller;

import cn.hutool.json.JSONUtil;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.file.model.dto.UploadFileDto;
import com.yixi.file.model.dto.UploadResultDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.model.vo.UploadFileVo;
import com.yixi.file.service.EFileService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yixi
 * @date 2023/8/21
 * @apiNote
 */
@Slf4j
@RestController()
@RequestMapping("/fileService")
public class UploadController {

    private final EFileService eFileService;
    private final RedisTemplate<String,Object> redisTemplate;

    public UploadController(EFileService eFileService,RedisTemplate redisTemplate){
        this.eFileService = eFileService;
        this.redisTemplate = redisTemplate;
    }

    @ApiOperation("检查md5")
    @GetMapping("/checkMD5")
    public BaseResponse fileCheckMD5( HttpServletRequest request,EFile eFile){
        System.out.println("efile is "+eFile);
        System.out.println("request is "+ request);

        log.info("检查是否是秒传",eFile);
        if (eFile == null || request == null) throw new BusinessException(EventCode.NULL_ERROR);

        boolean res = eFileService.fileCheckMD5(request,eFile); // 返回true 说明秒传
        return ResultUtils.success(res);
    }

    @ApiOperation("初始化分片")
    @PostMapping("/initChunk")
    public BaseResponse initChunk(UploadFileVo uploadFileVo,HttpServletRequest request){
        log.info("初始化分片");
        log.info("uploadFileVo = "+uploadFileVo);
        log.info("request = "+request);
        if (uploadFileVo == null || request == null) throw new BusinessException(EventCode.NULL_ERROR);
        UploadFileVo res = eFileService.initChunk(request, uploadFileVo);
        return ResultUtils.success(res);
    }

    @ApiOperation("分片上传")
    @PostMapping("/upload/chunk")
    public BaseResponse uploadChunk(UploadFileVo uploadFileVo,HttpServletRequest request){
        log.info("分片上传");
        log.info("uploadFileVo = "+uploadFileVo);
        log.info("fileSize is "+uploadFileVo.getFile().getSize());
        log.info("request = "+request);
        if (uploadFileVo == null || request == null) throw new BusinessException(EventCode.NULL_ERROR);
        UploadResultDto uploadResultDto = eFileService.uploadChunk(request, uploadFileVo);
        return ResultUtils.success(uploadResultDto);
    }

    @ApiOperation("获取文件上次分片索引")
    @GetMapping("/upload/lastChunk")
    public BaseResponse lastChunk(String ossChunkId){
        if(!StringUtils.hasLength(ossChunkId)){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        String redisParamJson = (String)(redisTemplate.opsForValue().get(ossChunkId));
        if(redisParamJson == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        UploadFileDto redisParam = new UploadFileDto();
        redisParam = JSONUtil.toBean(redisParamJson, UploadFileDto.class);
        System.out.println("redisParamJson"+redisParam);
        return ResultUtils.success(redisParam);
    }

}
