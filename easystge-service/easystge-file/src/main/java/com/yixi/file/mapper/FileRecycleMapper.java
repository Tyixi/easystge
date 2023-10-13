package com.yixi.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixi.file.model.entity.FileRecycle;
import com.yixi.file.model.vo.FileRecycleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 晨曦
* @description 针对表【file_recycle(文件回收站)】的数据库操作Mapper
* @createDate 2023-09-09 16:33:23
* @Entity generator.model/entity.FileRecycle
*/
@Mapper
public interface FileRecycleMapper extends BaseMapper<FileRecycle> {
    /**
     * 查询回收站文件详情信息
     * @param page
     * @param fileRecycleVo
     * @return
     */
    IPage<FileRecycleVo> getFileRecycleListDetail(@Param("page") Page page, @Param("fileRecycleVo") FileRecycleVo fileRecycleVo);

}




