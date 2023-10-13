package com.yixi.file.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.file.model.entity.FileRecycle;
import com.yixi.file.model.query.FileQuery;
import com.yixi.file.model.query.FileRecycleQuery;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.service.FileRecycleService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yixi
 * @date 2023/9/10
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/fileService/recycle")
public class FileRecycleController {

    private final FileRecycleService fileRecycleService;

    public FileRecycleController(FileRecycleService fileRecycleService){
        this.fileRecycleService = fileRecycleService;
    }

    /**
     * 分页查询
     * @return
     */
    @ApiOperation("查询回收站文件")
    @GetMapping("/list/page")
    public BaseResponse listByPage(HttpServletRequest request, FileRecycleQuery fileRecycleQuery){
        if (request == null || fileRecycleQuery == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        IPage<FileRecycleVo> result = fileRecycleService.getFileRecycleListDetail(request, fileRecycleQuery);
        return ResultUtils.success(result);
    }
}
