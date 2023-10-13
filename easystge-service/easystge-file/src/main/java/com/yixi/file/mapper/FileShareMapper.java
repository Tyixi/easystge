package com.yixi.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixi.file.model.entity.FileShare;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.model.vo.FileShareVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author yixi
*/
@Mapper
public interface FileShareMapper extends BaseMapper<FileShare> {

    /**
     * 查询分享文件详情信息
     * @param page
     * @param fileShareVo
     * @return
     */
    IPage<FileShareVo>  getFileShareListDetail(@Param("page") Page page, @Param("fileShareVo") FileShareVo fileShareVo);


    /**
     * 更新分享文件浏览次数
     * @param shareId
     */
    void updateShareShowCount(@Param("shareId") String shareId);

}




