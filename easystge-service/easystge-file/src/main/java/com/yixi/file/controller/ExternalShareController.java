package com.yixi.file.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yixi.common.constants.MConstant;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
import com.yixi.common.utils.ResultUtils;
import com.yixi.file.model.dto.ShareDto;
import com.yixi.file.model.entity.FileShare;
import com.yixi.file.model.query.FileShareQuery;
import com.yixi.file.model.vo.FileShareVo;
import com.yixi.file.model.vo.ShareInfoVo;
import com.yixi.file.service.FileShareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yixi
 */
@RestController
@RequestMapping("/fileService/exter/fileShare")
public class ExternalShareController {
    private final FileShareService fileShareService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ExternalShareController(FileShareService fileShareService,RedisTemplate redisTemplate){
        this.fileShareService = fileShareService;
        this.redisTemplate = redisTemplate;
    }


    @ApiOperation("获取登录信息")
    @GetMapping("/loginInfo/{shareId}")
    public BaseResponse getLoginInfo(HttpServletRequest request,@PathVariable("shareId") String shareId){
        String shareDtoJson = (String)(redisTemplate.opsForValue().get(MConstant.REDIS_SHARE_KEY + shareId));

        if (shareDtoJson == null){
            //
            return ResultUtils.success(null);
        }

        ShareDto shareDto = JSONUtil.toBean(shareDtoJson, ShareDto.class);

        ShareInfoVo shareInfo = fileShareService.getShareInfo(shareId);
        // 判断是否是当前用户分享的文件
        //前端可以通过这个判断两个按钮的显示，取消分享和保存到网盘
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (StringUtils.hasLength(userId) && userId.equals(shareDto.getUserId()) ){
            shareInfo.setCurrentUser(true);
        }else {
            shareInfo.setCurrentUser(false);
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

        // 将shareDto保存到redis
        redisTemplate.opsForValue().set(MConstant.REDIS_SHARE_KEY+fileShare.getShareId(),
                JSONUtil.toJsonStr(shareDto));

        return ResultUtils.success(null);
    }


    /**
     * 分页查询
     * @return
     */
    @ApiOperation("查询分享文件")
    @GetMapping("/list/page")
    public BaseResponse listByPage(HttpServletRequest request, FileShareQuery fileShareQuery){
        if (request == null || fileShareQuery == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        IPage<FileShareVo> result = fileShareService.getFileShareListDetail(request, fileShareQuery);
        return ResultUtils.success(result);
    }


}
