package com.yixi.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixi.file.model.entity.FileRecycle;
import com.yixi.file.model.query.FileRecycleQuery;
import com.yixi.file.model.vo.FileRecycleVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface FileRecycleService extends IService<FileRecycle> {
    /**
     * 查询用户回收站文件详情
     * @param request
     * @param fileRecycleQuery
     * @return
     */
    IPage<FileRecycleVo> getFileRecycleListDetail(HttpServletRequest request, FileRecycleQuery fileRecycleQuery);


}
