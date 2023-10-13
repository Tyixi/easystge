package com.yixi.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.file.model.dto.ShareDto;
import com.yixi.file.model.entity.FileShare;
import com.yixi.file.model.query.FileRecycleQuery;
import com.yixi.file.model.query.FileShareQuery;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.model.vo.FileShareVo;
import com.yixi.file.model.vo.ShareInfoVo;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
* @author yixi
*/
public interface FileShareService extends IService<FileShare> {

    /**
     * 查询用户分享文件详情
     * @param request
     * @param fileShareQuery
     * @return
     */
    IPage<FileShareVo> getFileShareListDetail(HttpServletRequest request, FileShareQuery fileShareQuery);

    /**
     * 分享文件
     * @param request
     * @param fileShare
     */
    @Transactional
    void saveFileShare(HttpServletRequest request, FileShare fileShare);

    /**
     * 批量删除分享文件
     * @param request
     * @param ids
     */
    @Transactional
    void delFileShareBatch(HttpServletRequest request, Set<String> ids);

    /**
     * 获取分享信息
     * @param shareId
     * @return
     */
    ShareInfoVo getShareInfo(String shareId);

    /**
     * 检验分享码
     * @param fileShare
     * @return
     */
    @Transactional
    ShareDto checkShareCode(FileShare fileShare);

}
