package com.yixi.file.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.common.utils.UserUtil;
import com.yixi.file.client.OssServiceClient;
import com.yixi.file.model.dto.FolderTreeDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.model.query.FileQuery;
import com.yixi.file.service.EFileService;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * @author yixi
 * @date 2023/8/15
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/fileService")
public class FileController {

    private final EFileService eFileService;
    private final OssServiceClient ossServiceClient;

    public FileController(EFileService eFileService,OssServiceClient ossServiceClient){
        this.eFileService = eFileService;
        this.ossServiceClient = ossServiceClient;
    }

    /**
     * 分页查询
     * @return
     */
    @ApiOperation("查询文件")
    @GetMapping("/list/page")
    public BaseResponse listFilesByPage(FileQuery fileQuery, HttpServletRequest request){
        System.out.println("查询文件: "+fileQuery);
        if(fileQuery == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        // 获取用户id
        String userId = UserUtil.getUserIdByRequest(request);
        Page<EFile> resultPage = eFileService.findUserFileList(userId, fileQuery);
        System.out.println("大小："+resultPage.getSize());
        return ResultUtils.success(resultPage);
    }



    /**
     * 删除文件
     * @param ids
     * @return
     */
    @ApiOperation("彻底删除文件")
    @DeleteMapping("/file/delete")
    public BaseResponse deleteFile(@RequestBody Set<String> ids, HttpServletRequest request){
        System.out.println("deleteFile ids is "+ids);
        if (request == null || ids == null){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }

        boolean res = eFileService.deleteFileBatch(request,ids);
        return ResultUtils.success(res);
    }




    @ApiOperation("新建目录")
    @PostMapping("/newFolder")
    public BaseResponse newFolder(@RequestBody EFile eFile, HttpServletRequest request){

        EFile new_folder = eFileService.newFolder(request, eFile);
        return ResultUtils.success(new_folder);
    }


    @ApiOperation("获取文件地址")
    @GetMapping("/fileNav")
    public BaseResponse getFileNav(String path){
        if (!StringUtils.hasLength(path)){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        return ResultUtils.success("");
    }


    @ApiOperation("获取文件目录树")
    @GetMapping("/folder/tree")
    public BaseResponse folderTree(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        List<FolderTreeDto> folderTree = eFileService.getFolderTree(request);

        return ResultUtils.success(folderTree);
    }

    @ApiOperation("文件重命名")
    @PutMapping("/file/rename")
    public BaseResponse fileRename(@RequestBody EFile eFile, HttpServletRequest request){
        if (eFile == null || request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        EFile result = eFileService.fileRename(request, eFile);
        return ResultUtils.success(result);
    }

    @ApiOperation("文件批量移除到回收站")
    @PutMapping("/file/toRecycleBatch")
    public BaseResponse file2RecycleBatch(@RequestBody Set<String> ids, HttpServletRequest request){
        if (ids == null || ids.isEmpty() || request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        boolean res = eFileService.removeFile2RecycleBatch(request, ids);

        return ResultUtils.success(res);
    }

    @ApiOperation("回收站文件批量还原")
    @PutMapping("/file/recoverBatch")
    public BaseResponse recoverFilesBatch(@RequestBody Set<String> ids, HttpServletRequest request){
        if (ids == null || ids.isEmpty() || request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        eFileService.recoverFileBatch(request, ids);
        return ResultUtils.success(true);
    }



}
