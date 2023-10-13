package com.yixi.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.file.model.dto.FolderTreeDto;
import com.yixi.file.model.dto.UploadResultDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.model.query.FileQuery;
import com.yixi.file.model.vo.UploadFileVo;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author yixi
 * @apiNote
 */
public interface EFileService extends IService<EFile> {
    /**
     *
     * @param eFile
     * @return
     */
    @Transactional
    public boolean fileCheckMD5(HttpServletRequest request, EFile eFile);


    /**
     * 初始化分片
     * @param request
     * @param uploadFileVo
     * @return
     */
    UploadFileVo initChunk(HttpServletRequest request, UploadFileVo uploadFileVo);

    /**
     * 分片上传
     * @param request
     * @param uploadFileVo
     * @return
     */
    @Transactional
    UploadResultDto uploadChunk(HttpServletRequest request, UploadFileVo uploadFileVo);

    /**
     * 获取文件列表
     * @param request
     * @param fileQuery
     * @return
     */
    Page<EFile> findUserFileList(HttpServletRequest request, FileQuery fileQuery);

    /**
     * 用户批量删除文件
     * @param request
     * @param ids
     * @return
     */
    @Transactional
    boolean deleteFileBatch(HttpServletRequest request, Set<String> ids);

    /**
     * 文件批量移除回收站
     * @param request
     * @param ids
     * @return
     */
    @Transactional
    boolean removeFile2RecycleBatch(HttpServletRequest request, Set<String> ids);

    /**
     * 批量还原文件
     * @param request
     * @param ids
     */
    @Transactional
    void recoverFileBatch(HttpServletRequest request, Set<String> ids);

    /**
     * 新建文件夹
     * @param request
     * @param eFile
     * @return
     */
    @Transactional
    EFile newFolder(HttpServletRequest request, EFile eFile);

    /**
     * 获取文件地址
     * @param request
     * @param path
     * @return
     */
    List<EFile> getFileNav(HttpServletRequest request, String path);


    /**
     * 获取文件目录树
     * @param request
     * @return
     */
    List<FolderTreeDto> getFolderTree(HttpServletRequest request);

    /**
     * 文件重命名
     * @param request
     * @param eFile
     * @return
     */
    EFile fileRename(HttpServletRequest request,EFile eFile);

    /**
     * 创建下载链接返回code
     * @param request
     * @param fileId
     * @return
     */
    String createDownloadCode(HttpServletRequest request,String fileId);


}
