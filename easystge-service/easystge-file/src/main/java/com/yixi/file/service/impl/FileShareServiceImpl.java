package com.yixi.file.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yixi.common.constants.FileRecycleConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
import com.yixi.file.client.UcenterClient;
import com.yixi.file.mapper.EFileMapper;
import com.yixi.file.model.dto.ShareDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.model.entity.FileShare;
import com.yixi.file.model.enums.FileDelFlagEnums;
import com.yixi.file.model.enums.ShareValidTypeEnums;
import com.yixi.file.model.query.FileRecycleQuery;
import com.yixi.file.model.query.FileShareQuery;
import com.yixi.file.model.vo.FileRecycleVo;
import com.yixi.file.model.vo.FileShareVo;
import com.yixi.file.model.vo.ShareInfoVo;
import com.yixi.file.service.EFileService;
import com.yixi.file.service.FileShareService;
import com.yixi.file.mapper.FileShareMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

/**
* @author yixi
*/
@Service
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare>
    implements FileShareService{

    private final FileShareMapper fileShareMapper;
    private final EFileMapper eFileMapper;
    private final UcenterClient ucenterClient;

    public FileShareServiceImpl(FileShareMapper fileShareMapper,EFileMapper eFileMapper,UcenterClient ucenterClient){
        this.fileShareMapper = fileShareMapper;
        this.eFileMapper = eFileMapper;
        this.ucenterClient = ucenterClient;
    }


    @Override
    public IPage<FileShareVo> getFileShareListDetail(HttpServletRequest request, FileShareQuery fileShareQuery) {
        if (request == null || fileShareQuery == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        FileShareVo fileShareVo = new FileShareVo();
        // 保存用户id
        fileShareVo.setUserId(userId);
        // 查询回收站文件数据
        Page<FileShareVo> page = new Page<>(fileShareQuery.getPageNum(),fileShareQuery.getPageSize());
        IPage<FileShareVo> fileShareListDetail = fileShareMapper.getFileShareListDetail(page,fileShareVo);
        // 设置数据
        for (int i = 0; i < fileShareListDetail.getRecords().size(); i++) {
            // 永久有效
            if (fileShareListDetail.getRecords().get(i).getEndTime() == null){
                fileShareListDetail.getRecords().get(i).setValidTime(-1);
                continue;
            }
            // 计算文件失效时间  单位：天
            // 当前日期
            Date currentDate = DateUtil.date();
            // 回收站文件保存结束日期
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localDateTime = fileShareListDetail.getRecords().get(i).getEndTime();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            Date endTime = Date.from(zdt.toInstant());
            // 计算出剩余几天
            long betweenDay = DateUtil.between(currentDate, endTime, DateUnit.DAY);
            fileShareListDetail.getRecords().get(i).setValidTime(betweenDay);
        }

        return fileShareListDetail;
    }

    @Override
    public void saveFileShare(HttpServletRequest request, FileShare fileShare) {
        // 校验
        if (request == null || fileShare == null || !StringUtils.hasLength(fileShare.getFileId())){
            throw new BusinessException(EventCode.NULL_ERROR);
        }


        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId ==  null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        // 保存用户id
        fileShare.setUserId(userId);

        // 设置分享类型
        ShareValidTypeEnums validTypeEnums = ShareValidTypeEnums.getByType(fileShare.getValidType());

        if(validTypeEnums == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 如果分享文件不是永久有效
        if(ShareValidTypeEnums.FOREVER != validTypeEnums){
            fileShare.setEndTime(LocalDateTime.now().plusDays(validTypeEnums.getDays())); // 保存时间长度
        }

        // 自定义分享码
        if (!StringUtils.hasLength(fileShare.getShareCode())){
            fileShare.setShareCode(RandomUtil.randomString(4)); // 随机生成长度为5的字符串
        }

        this.baseMapper.insert(fileShare);


    }

    @Override
    public void delFileShareBatch(HttpServletRequest request, Set<String> ids) {
        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId ==  null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.in("share_id", ids);
        int delete = this.baseMapper.delete(queryWrapper);
    }

    @Override
    public ShareInfoVo getShareInfo(String shareId) {

        FileShare fileShare = this.baseMapper.selectById(shareId);
        LocalDateTime localDateTime = LocalDateTime.now();
        //链接失效或者fileShare为空
        if (null == fileShare || (fileShare.getEndTime()!=null && !fileShare.getEndTime().isAfter(localDateTime))){
            throw new BusinessException(EventCode.FILE_SHARE_INVALID);
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("file_id", fileShare.getFileId());
        queryWrapper.eq("user_id", fileShare.getUserId());
        EFile eFile = eFileMapper.selectOne(queryWrapper);

        // 文件为不可用状态
        if (null == eFile || !FileDelFlagEnums.USING.getFlag().equals(eFile.getDelFlag())){
            throw new BusinessException(EventCode.FILE_SHARE_INVALID);
        }

        // 远程请求获取分享文件的用户信息
        UserInfo userInfo = ucenterClient.getUserInfoOrder(fileShare.getUserId());

        // 保存分享文件信息
        ShareInfoVo shareInfoVo = new ShareInfoVo();
        shareInfoVo.setUserId(fileShare.getUserId());
        shareInfoVo.setFileId(fileShare.getFileId());
        shareInfoVo.setShareTime(fileShare.getCreateTime());
        shareInfoVo.setAvatar(userInfo.getAvatar());
        shareInfoVo.setNickName(userInfo.getNickName());
        shareInfoVo.setFileName(eFile.getFileName());

        if (fileShare.getEndTime() == null){
            shareInfoVo.setValidTime(-1);
        }else{
            // 计算文件失效时间  单位：天
            // 当前日期
            Date currentDate = DateUtil.date();
            // 回收站文件保存结束日期
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime ldt = fileShare.getEndTime();
            ZonedDateTime zdt = ldt.atZone(zoneId);
            Date endTime = Date.from(zdt.toInstant());
            // 计算出剩余几天
            long betweenDay = DateUtil.between(currentDate, endTime, DateUnit.DAY);
            shareInfoVo.setValidTime(betweenDay);
        }

        return shareInfoVo;
    }

    @Override
    public ShareDto checkShareCode(FileShare fileShare) {
        FileShare sel_fileShare = this.baseMapper.selectById(fileShare.getShareId());
        LocalDateTime localDateTime = LocalDateTime.now();
        //链接失效或者fileShare为空
        if (null == sel_fileShare || (sel_fileShare.getEndTime()!=null && !sel_fileShare.getEndTime().isAfter(localDateTime))){
            throw new BusinessException(EventCode.FILE_SHARE_INVALID);
        }

        // 提取码错误
        if (!sel_fileShare.getShareCode().equals(fileShare.getShareCode())){
            throw new BusinessException(EventCode.SHARE_CODE_ERROR);
        }

        // 更新浏览次数
        this.baseMapper.updateShareShowCount(fileShare.getShareId());

        ShareDto shareDto = new ShareDto();
        shareDto.setShareId(fileShare.getShareId());
        shareDto.setUserId(sel_fileShare.getUserId());
        shareDto.setFileId(sel_fileShare.getFileId());
        shareDto.setEndTime(sel_fileShare.getEndTime());


        return shareDto;
    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime af = LocalDateTime.of(2023, 10, 12, 00, 39);

        System.out.println(localDateTime);
        System.out.println(af);
        System.out.println(af.isAfter(localDateTime));
    }

}




