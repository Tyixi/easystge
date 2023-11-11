package com.yixi.file.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
import com.yixi.file.mapper.FileRecycleMapper;
import com.yixi.file.model.entity.FileRecycle;
import com.yixi.file.model.query.FileRecycleQuery;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.service.FileRecycleService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
* @author yixi
*/
@Service
public class FileRecycleServiceImpl extends ServiceImpl<FileRecycleMapper, FileRecycle>
    implements FileRecycleService {
    private final FileRecycleMapper fileRecycleMapper;

    public FileRecycleServiceImpl(FileRecycleMapper fileRecycleMapper){
        this.fileRecycleMapper = fileRecycleMapper;
    }

    @Override
    public IPage<FileRecycleVo> getFileRecycleListDetail(HttpServletRequest request, FileRecycleQuery fileRecycleQuery) {
        if (request == null || fileRecycleQuery == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        FileRecycleVo fileRecycleVo = new FileRecycleVo();
        // 保存用户id
        fileRecycleVo.setUserId(userId);

        // 查询回收站文件数据
        Page<FileRecycleVo> page = new Page<>(fileRecycleQuery.getPageNum(),fileRecycleQuery.getPageSize());
        IPage<FileRecycleVo> fileRecycleListDetail = fileRecycleMapper.getFileRecycleListDetail(page,fileRecycleVo);
        for (int i = 0; i < fileRecycleListDetail.getRecords().size(); i++) {
            // 计算文件在回收站的剩余时间  单位：天
            // 当前日期
            Date currentDate = DateUtil.date();
            // 回收站文件保存结束日期
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localDateTime = fileRecycleListDetail.getRecords().get(i).getEndTime();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            Date endTime = Date.from(zdt.toInstant());
            // 计算出剩余几天
            long betweenDay = DateUtil.between(currentDate, endTime, DateUnit.DAY);
            fileRecycleListDetail.getRecords().get(i).setValidTime(betweenDay);
        }

        return fileRecycleListDetail;
    }

    public static void main(String[] args) {
//        String dateStr1 = "2023-10-06 0:42:59";
//        Date date1 = DateUtil.date();
//
//        String dateStr2 = "2023-10-04 00:42:59";
//        Date date2 = DateUtil.parse(dateStr2);
//
//        //相差一个月，31天
//        long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);


        //  Sun Nov 05 13:34:08 CST 2023
        //        String dateStr1 = "2023-10-06 0:42:59";
        Date date1 = DateUtil.date();
//
        String dateStr2 = "2023-11-05 11:51:00";
        Date date2 = DateUtil.parse(dateStr2);
        long betweenMin = DateUtil.between(date1, date2, DateUnit.MINUTE);
        System.out.println("betweenDay is "+betweenMin);
    }
}




