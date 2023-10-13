package com.yixi.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.file.model.entity.FileShare;
import com.yixi.file.model.query.FileRecycleQuery;
import com.yixi.file.model.query.FileShareQuery;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.model.vo.FileShareVo;
import com.yixi.file.service.FileShareService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author yixi
 * @date 2023/9/24
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/fileService/fileShare")
public class FileShareController {
    private final FileShareService fileShareService;
    public FileShareController(FileShareService fileShareService){
        this.fileShareService = fileShareService;
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

    @ApiOperation("分享文件")
    @PostMapping("/share")
    public BaseResponse shareFile(HttpServletRequest request,@RequestBody FileShare fileShare){
        if (request == null || fileShare == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        System.out.println("shareFile is "+fileShare);
        fileShareService.saveFileShare(request, fileShare);
        return ResultUtils.success(fileShare);
    }

    @ApiOperation("删除分享")
    @DeleteMapping("/del")
    public BaseResponse delShare(@RequestBody Set<String> ids, HttpServletRequest request){
        if (request == null || ids == null || ids.isEmpty()){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        fileShareService.delFileShareBatch(request, ids);
        return ResultUtils.success(true);
    }


}
