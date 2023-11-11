package com.yixi.file.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixi.common.constants.MConstant;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.*;
import com.yixi.file.model.dto.ShareDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.model.entity.FileShare;
import com.yixi.file.model.query.FileQuery;
import com.yixi.file.model.query.FileShareQuery;
import com.yixi.file.model.vo.FileShareVo;
import com.yixi.file.model.vo.ShareInfoVo;
import com.yixi.file.service.EFileService;
import com.yixi.file.service.FileShareService;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.Even;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yixi
 */
@RestController
@RequestMapping("/fileService/exter/fileShare")
public class ExternalShareController {
    private final FileShareService fileShareService;
    private final EFileService eFileService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ExternalShareController(FileShareService fileShareService,RedisTemplate redisTemplate,EFileService eFileService){
        this.fileShareService = fileShareService;
        this.eFileService = eFileService;
        this.redisTemplate = redisTemplate;
    }


    @ApiOperation("获取登录信息")
    @GetMapping("/loginInfo/{shareId}")
    public BaseResponse getLoginInfo(HttpServletRequest request,@PathVariable("shareId") String shareId){
        String shareDtoJson = (String)(redisTemplate.opsForValue().get(MConstant.REDIS_SHARE_KEY + shareId));

        if (shareDtoJson == null){
            return ResultUtils.success(null);
        }

        ShareDto shareDto = JSONUtil.toBean(shareDtoJson, ShareDto.class);
        ShareInfoVo shareInfo = fileShareService.getShareInfo(shareId);
        // 判断是否是当前用户分享的文件
        //前端可以通过这个判断两个按钮的显示，取消分享和保存到网盘

        shareInfo.setCurrentUser(false);
        try {
            String userId = JwtUtils.getUserIdByJwtToken(request);
            if (StringUtils.hasLength(userId) && userId.equals(shareDto.getUserId()) ){
                shareInfo.setCurrentUser(true);
            }
        }catch (Exception e){
            e.printStackTrace(); // 打印异常信息
            return ResultUtils.success(shareInfo);
        }
        return ResultUtils.success(shareInfo);
    }


    @ApiOperation("获取分享文件信息")
    @GetMapping("/shareInfo")
    public BaseResponse getFileShareInfo(FileShare fileShare){
        System.out.println("fileShare is "+fileShare);
        if (fileShare == null || !StringUtils.hasLength(fileShare.getShareId())){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        ShareInfoVo shareInfo = fileShareService.getShareInfo(fileShare.getShareId());
        return ResultUtils.success(shareInfo);
    }


    @ApiOperation("校验提取码")
    @PostMapping("/checkShareCode")
    public BaseResponse checkShareCode(@RequestBody FileShare fileShare){
        ShareDto shareDto = fileShareService.checkShareCode(fileShare);

        // 将shareDto保存到redis 保存2分钟
        redisTemplate.opsForValue().set(MConstant.REDIS_SHARE_KEY+fileShare.getShareId(),
                JSONUtil.toJsonStr(shareDto),MConstant.REDIS_SHARE_CODE_SAVE_TIME, TimeUnit.MINUTES);

        return ResultUtils.success(null);
    }


    /**
     * 分页查询
     * @return
     */
    @ApiOperation("查询分享文件")
    @GetMapping("/list/page")
    public BaseResponse listByPage(HttpServletRequest request, FileShareQuery fileShareQuery){
        System.out.println("fileShareQuery is "+fileShareQuery);
        if (request == null || fileShareQuery == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        String shareDtoJson = (String)(redisTemplate.opsForValue().get(MConstant.REDIS_SHARE_KEY + fileShareQuery.getShareId()));

        // 检验分享验证是否失效
        if (null == shareDtoJson){
            throw new BusinessException(EventCode.SHARE_VERIFY_INVALID);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        ShareDto shareDto = JSONUtil.toBean(shareDtoJson, ShareDto.class);
        // 分享文件失效
        if (shareDto.getEndTime() != null && localDateTime.isAfter(shareDto.getEndTime())){
            throw new BusinessException(EventCode.FILE_SHARE_INVALID);
        }

        // TODO: 2023/11/7 逻辑有问题 
        FileQuery fileQuery = new FileQuery();
        System.out.println("是否if "+(!StringUtils.hasLength(fileShareQuery.getFilePid()) && !"0".equals(fileShareQuery.getFilePid())));
        if (StringUtils.hasLength(fileShareQuery.getFilePid()) && !"0".equals(fileShareQuery.getFilePid())){
            System.out.println("不等于0"+shareDto.getFileId());
            eFileService.checkRootFilePid(shareDto.getFileId(), shareDto.getUserId(), fileShareQuery.getFilePid());
            fileQuery.setFilePid(fileShareQuery.getFilePid());
        }else {
            System.out.println("pid 不等于0");
            fileQuery.setFileId(shareDto.getFileId());
        }
        System.out.println("fileQuery is "+fileQuery);

        Page<EFile> resultPage = eFileService.findUserFileList(shareDto.getUserId(), fileQuery);
        return ResultUtils.success(resultPage);
    }





}
