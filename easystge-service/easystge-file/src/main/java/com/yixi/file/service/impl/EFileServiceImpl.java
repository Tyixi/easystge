package com.yixi.file.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yixi.common.constants.EmailConstant;
import com.yixi.common.constants.FileRecycleConstant;
import com.yixi.common.constants.MConstant;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.JwtUtils;
import com.yixi.file.client.KodoClient;
import com.yixi.file.client.OssServiceClient;
import com.yixi.file.client.UcenterClient;
import com.yixi.file.mapper.EFileMapper;
import com.yixi.file.model.dto.DownloadFileDto;
import com.yixi.file.model.dto.FolderTreeDto;
import com.yixi.file.model.dto.UploadResultDto;
import com.yixi.file.model.entity.EFile;
//import com.yixi.file.model.enums.FileDelFlagEnums;
import com.yixi.file.model.entity.FileRecycle;
import com.yixi.file.model.entity.User;
import com.yixi.file.model.enums.FileDelFlagEnums;
import com.yixi.file.model.enums.FileFolderTypeEnums;
import com.yixi.file.model.enums.FileTypeEnums;
import com.yixi.file.model.enums.UploadStatusEnums;
import com.yixi.file.model.query.FileQuery;
import com.yixi.file.model.vo.UploadFileVo;
import com.yixi.file.service.EFileService;
import com.yixi.file.service.FileRecycleService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author yixi
*/
@Service
public class EFileServiceImpl extends ServiceImpl<EFileMapper, EFile>
    implements EFileService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EFileMapper eFileMapper;
    private final FileRecycleService fileRecycleService;
    private final UcenterClient ucenterClient;
    private final KodoClient kodoClient;
    private final OssServiceClient ossServiceClient;
    private final static Integer minChunkSize = 100;


    public EFileServiceImpl(RedisTemplate redisTemplate, EFileMapper eFileMapper, UcenterClient ucenterClient, KodoClient kodoClient, OssServiceClient ossServiceClient,FileRecycleService fileRecycleService){
        this.redisTemplate = redisTemplate;
        this.eFileMapper = eFileMapper;
        this.fileRecycleService = fileRecycleService;
        this.ucenterClient = ucenterClient;
        this.kodoClient = kodoClient;
        this.ossServiceClient = ossServiceClient;
    }

    @Override
    public boolean fileCheckMD5(HttpServletRequest request,EFile eFile) {
        boolean result = false;
        QueryWrapper<EFile> wrapper = new QueryWrapper<>();
        wrapper.eq("file_md5", eFile.getFileMd5());
        wrapper.last("limit 1");
        EFile select_eFile = baseMapper.selectOne(wrapper);
        if (select_eFile != null){  // 秒传
            String userId = JwtUtils.getUserIdByJwtToken(request);
            if (userId == null){
                throw new BusinessException(EventCode.PARAMS_ERROR);
            }
            // 根据用户id获取用户信息
            //从redis中获取
            UserInfo userInfo = null;
            String userJson = (String)(redisTemplate.opsForValue().get(UserConstant.USER_LOGIN_INFO + userId));
            if (userJson != null){
                userInfo = JSONUtil.toBean(userJson, UserInfo.class);
            }else{
                // redis里没有 远程调用用户中心获取用户数据
                userInfo = ucenterClient.getUserInfoOrder(userId);
            }

            // 检查用户的可用空间是否足够
            if (select_eFile.getFileSize() + userInfo.getUseSpace() > userInfo.getTotalSpace()){
                // 空间不够
                throw new BusinessException(EventCode.ACCOUNT_SPACE_INSUFFICIENT, "空间不足");
            }
            select_eFile.setFileId(null);
            select_eFile.setFileMd5(eFile.getFileMd5());
            select_eFile.setCreateTime(new Date());
            select_eFile.setFilePid(eFile.getFilePid());
            select_eFile.setIsDeleted(0);
            select_eFile.setStatus(2);
            select_eFile.setUserId(userId);
            // 文件重命名 同一文件夹下相同文件名称不能重复
            eFile.setFileName(fileRename(eFile.getFilePid(), userId, eFile.getFileName()));
            select_eFile.setFileName(eFile.getFileName());

            // 将文件信息保存到数据库
            this.baseMapper.insert(select_eFile);

            // 更新用户使用空间
            UserInfo updateUser = new UserInfo();
            updateUser.setUserId(userId);
            updateUser.setUseSpace(select_eFile.getFileSize() + userInfo.getUseSpace());
            // 远程调用更新
            BaseResponse baseResponse = ucenterClient.updateUser(updateUser);
            if (baseResponse.getCode() != 0){
                throw new BusinessException(EventCode.SYSTEM_ERROR);
            }
            userInfo.setUseSpace(select_eFile.getFileSize() + userInfo.getUseSpace());
            // 更新redis中的用户信息
            try {
                redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_INFO+userInfo.getUserId(),
                        JSONUtil.toJsonStr(userInfo));
            }catch (Exception e){
                throw new BusinessException(EventCode.SYSTEM_ERROR);
            }
            result = true;
        }


        return result;
    }



    @Override
    public UploadFileVo initChunk(HttpServletRequest request, UploadFileVo uploadFileVo) {

        UploadFileVo result = new UploadFileVo();

        if (request == null || uploadFileVo == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 生成任务id
        String taskId = DateUtil.format(new Date(), "yyyy/MM/dd")+"/"+UUID.randomUUID().toString().replaceAll("_", "");
        result.setTaskId(taskId);

        //String taskKey = userId+uploadFileVo.getFileName()+taskId;
        String taskKey = taskId+uploadFileVo.getFileName();

        // 远程调用 阿里云oss获取分片唯一ID
        BaseResponse baseResponse = ossServiceClient.getOssChunkId(taskKey);
        if (baseResponse == null || baseResponse.getCode() != 0 || baseResponse.getData() == null){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }
        // 唯一ID
        String ossChunkId = (String)(baseResponse.getData());
        result.setOssChunkId(ossChunkId);
        result.setMinSliceSize(minChunkSize);

        //保存到redis
        redisTemplate.opsForValue().set(ossChunkId, JSONUtil.toJsonStr(result));
        return result;
    }

    @Override
    public UploadResultDto uploadChunk(HttpServletRequest request, UploadFileVo uploadFileVo) {
        if (request == null || uploadFileVo == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        // 根据用户id获取用户信息
        //从redis中获取
        UserInfo userInfo = null;
        String userJson = (String)(redisTemplate.opsForValue().get(UserConstant.USER_LOGIN_INFO + userId));
        if (userJson != null){
            userInfo = JSONUtil.toBean(userJson, UserInfo.class);
        }else{
            // redis里没有 远程调用用户中心获取用户数据
            userInfo = ucenterClient.getUserInfoOrder(userId);
        }

        // 检查磁盘空间
        // 从redis取出以保存的分片大小 再加上当前分片大小，查看用户空间是否足够
        // 目前上传的分片总大小  用 userId+taskId
        Long currentTempSize = currentTempSize = getRedisChunkSize(userId, uploadFileVo.getTaskId());

        if (uploadFileVo.getFile().getSize() + currentTempSize + userInfo.getUseSpace() > userInfo.getTotalSpace()){
            // 空间不够
            throw new BusinessException(EventCode.ACCOUNT_SPACE_INSUFFICIENT, "空间不足");
        }

        // 远程请求  上传分片
        BaseResponse baseResponse = null;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("taskId",uploadFileVo.getTaskId());
            map.put("fileName",uploadFileVo.getFileName());
            map.put("chunkIndex",uploadFileVo.getChunkIndex());
            map.put("chunks",uploadFileVo.getChunks());
            map.put("ossChunkId",uploadFileVo.getOssChunkId());
            map.put("fileMd5",uploadFileVo.getFileMd5());
            baseResponse = ossServiceClient.uploadChunk(map,uploadFileVo.getFile());
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(EventCode.SYSTEM_ERROR,e.getMessage());
        }

        if (baseResponse == null || baseResponse.getCode() != 0){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }

        UploadResultDto resultDto = new UploadResultDto();
        String fileUrl = (String) baseResponse.getData();

        // 更新用户使用空间
        UserInfo updateUser = new UserInfo();
        updateUser.setUserId(userId);
        updateUser.setUseSpace(uploadFileVo.getFile().getSize() + userInfo.getUseSpace());

        // 远程调用更新用户
        BaseResponse updateUserRes = ucenterClient.updateUser(updateUser);
        if (baseResponse.getCode() != 0){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }
        userInfo.setUseSpace(uploadFileVo.getFile().getSize() + userInfo.getUseSpace());
        // 更新redis中的用户信息
        try {
            redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_INFO+userInfo.getUserId(),
                    JSONUtil.toJsonStr(userInfo));
        }catch (Exception e){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }


        if (StringUtils.hasLength(fileUrl)){
            // 文件上传完成
            resultDto.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());  // 设置上传转态

            // 生成文件缩略图
            // TODO: 2023/8/29 生成文件缩略图
            // 文件信息保存到数据库
            EFile eFile_save = new EFile();
            FileTypeEnums fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(FileNameUtil.extName(fileUrl));
            eFile_save.setFileMd5(uploadFileVo.getFileMd5());
            eFile_save.setCreateTime(new Date());
            eFile_save.setUpdateTime(new Date());
            eFile_save.setFilePid(uploadFileVo.getFilePid());
            eFile_save.setIsDeleted(0);
            eFile_save.setStatus(2);
            eFile_save.setUserId(userId);
            eFile_save.setFileSize(uploadFileVo.getFileTotalSize());
            eFile_save.setFilePath(fileUrl);
            // folder_type 0 文件 1：目录
            eFile_save.setFolderType(FileFolderTypeEnums.FILE.getType());
            eFile_save.setFileCategory(fileTypeEnums.getCategory().getCategory());
            // file_type 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:zip 9:其他
            eFile_save.setFileType(fileTypeEnums.getType());
            // 文件重命名 同一文件夹下相同文件名称不能重复
            eFile_save.setFileName(fileRename(uploadFileVo.getFilePid(), userId, uploadFileVo.getFileName()));
            // 将文件信息保存到数据库
            this.baseMapper.insert(eFile_save);
            // 清除redis中缓存
            redisTemplate.delete(userId + uploadFileVo.getTaskId());    // 清除已上传分片大小
            return resultDto;
        }

        //  还有分片未上传
        resultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());  // 设置上传状态

        // 保存临时分片大小
        redisTemplate.opsForValue().set(userId + uploadFileVo.getTaskId(), currentTempSize + uploadFileVo.getFile().getSize());
        return resultDto;
    }

    @Override
    public Page<EFile> findUserFileList(HttpServletRequest request, FileQuery fileQuery) {
        if (request == null || fileQuery == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        fileQuery.setUserId(userId);
        EFile eFile = new EFile();
        BeanUtil.copyProperties(fileQuery, eFile);
        Page<EFile> page = new Page<>(fileQuery.getPageNum(), fileQuery.getPageSize());
        QueryWrapper<EFile> queryWrapper = new QueryWrapper<>(eFile);
        queryWrapper.orderByDesc("folder_type","update_time");
        Page<EFile> resultPage = page(page, queryWrapper);
        return resultPage;
    }

    @Override
    public boolean deleteFileBatch(HttpServletRequest request, Set<String> ids) {
        if (request == null || ids == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 查找要还原的文件
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("user_id", userId);
        queryWrapper1.in("file_id", ids);
        queryWrapper1.eq("del_flag", FileDelFlagEnums.RECYCLE.getFlag());
        List<EFile> sel_fileList = this.baseMapper.selectList(queryWrapper1);

        // 找到子目录文件
        List<String> delFilesSubFolderFileIdList = new ArrayList<>();
        for (EFile eFile : sel_fileList){
            if (FileFolderTypeEnums.FOLDER.getType().equals(eFile.getFolderType())){
                findAllSubFolderFileList(delFilesSubFolderFileIdList,userId,eFile.getFileId(),FileDelFlagEnums.DEL.getFlag());
            }
        }

        // 逻辑删除所有选的文件子目录中的文件
        if (!delFilesSubFolderFileIdList.isEmpty()){
            EFile eFile = new EFile();
            eFile.setIsDeleted(1);
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("user_id", userId);
            queryWrapper2.eq("del_flag", FileDelFlagEnums.DEL.getFlag());
            queryWrapper2.in("file_pid",delFilesSubFolderFileIdList);
            queryWrapper2.eq("is_deleted", 0);
            this.baseMapper.update(eFile,queryWrapper2);
        }

        // 删除所选文件
            // 先删除文件在回收站中的信息
        QueryWrapper queryWrapper3 = new QueryWrapper();
        queryWrapper3.eq("user_id", userId);
        queryWrapper3.in("file_id", ids);
        fileRecycleService.remove(queryWrapper3);

            // 逻辑删除所选文件
        EFile eFile = new EFile();
        eFile.setIsDeleted(1);
        QueryWrapper queryWrapper4 = new QueryWrapper();
        queryWrapper4.eq("user_id", userId);
        queryWrapper4.in("file_id", ids);
        queryWrapper4.eq("del_flag", FileDelFlagEnums.RECYCLE.getFlag());
        queryWrapper4.eq("is_deleted", 0);
        this.baseMapper.update(eFile,queryWrapper3);

        // 更新用户空间
            // 查询用户使用空间
        Long useSpace = this.baseMapper.selectUserUseSpace(userId);
        UserInfo updateUser = new UserInfo();
        updateUser.setUserId(userId);
        updateUser.setUseSpace(useSpace);
        // 远程调用更新用户信息
        BaseResponse updateUserResponse = ucenterClient.updateUser(updateUser);
        if (updateUserResponse.getCode() != 0){
            throw new BusinessException(EventCode.SYSTEM_ERROR);
        }

        // 更新用户缓存信息 - 使用空间
        UserInfo redisUserInfo = null;
        String userJson = (String)(redisTemplate.opsForValue().get(UserConstant.USER_LOGIN_INFO + userId));
            // 说明redis有缓存用户信息，进行更新
        if (userJson != null){
            redisUserInfo = JSONUtil.toBean(userJson, UserInfo.class);
            redisUserInfo.setUseSpace(useSpace);
            redisTemplate.opsForValue().set(UserConstant.USER_LOGIN_INFO+redisUserInfo.getUserId(),
                    JSONUtil.toJsonStr(redisUserInfo));
        }

        return true;
    }


    @Override
    public boolean removeFile2RecycleBatch(HttpServletRequest request, Set<String> ids) {
        if (request == null || ids == null || ids.isEmpty()){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("user_id", userId);
        queryWrapper1.in("file_id", ids);
        List<EFile> sel_files = this.baseMapper.selectList(queryWrapper1);
        if (sel_files.isEmpty()){
            // 说明文件已经在回收站了 直接返回
            return true;
        }

        // 删除的文件夹 要将他所有的子目录查出来
        List<String> delFilePidList = new ArrayList<>();
        for (EFile eFile : sel_files){
            findAllSubFolderFileList(delFilePidList, userId, eFile.getFileId(), FileDelFlagEnums.USING.getFlag());
        }
        if (!delFilePidList.isEmpty()){
            // 将所有移除目录设置到回收站
            EFile eFile = new EFile();
            eFile.setRecoveryTime(new Date());  // 设置进入回收站时间
            eFile.setDelFlag(FileDelFlagEnums.DEL.getFlag());
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("user_id", userId);
            //将查询出来的文件夹下的文件全部移除到回收站
            queryWrapper2.in("file_pid",delFilePidList);
            queryWrapper2.eq("del_flag", FileDelFlagEnums.USING.getFlag());
            this.baseMapper.update(eFile,queryWrapper2);
        }

        // 将选中的文件 delFlag 设置为回收站状态
        EFile eFile = new EFile();
        eFile.setRecoveryTime(new Date());
        eFile.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        QueryWrapper queryWrapper3 = new QueryWrapper();
        queryWrapper3.eq("user_id", userId);
        queryWrapper3.in("file_id", ids);
        queryWrapper3.eq("del_flag", FileDelFlagEnums.USING.getFlag());
        this.baseMapper.update(eFile,queryWrapper3);

        // 将选中的文件更新到回收站
        List<FileRecycle> fileRecycles = new ArrayList<>();
        System.out.println("选中文件  迭代输出");
        for(EFile tempFile : sel_files){
            FileRecycle fileRecycle = new FileRecycle();
            fileRecycle.setFileId(tempFile.getFileId());
            fileRecycle.setUserId(userId);
            fileRecycle.setFileName(tempFile.getFileName());
            fileRecycle.setEndTime(LocalDateTime.now().plusDays(FileRecycleConstant.FILE_RECYCLE_SAVE_TIME)); // 保存时间长度
            fileRecycles.add(fileRecycle);
        }

        System.out.println("要保存到回收站的数据数量 "+fileRecycles.size());

        // 保存
        fileRecycleService.saveBatch(fileRecycles);

        return true;
    }

    @Override
    public void recoverFileBatch(HttpServletRequest request, Set<String> ids) {
        if (request == null || ids == null || ids.isEmpty()){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        System.out.println("ids  迭代输出");
        Iterator<String> iterator1 = ids.iterator();
        while (iterator1.hasNext()){
            System.out.println(iterator1.next());
        }


        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 查找要还原的文件
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("user_id", userId);
        queryWrapper1.in("file_id", ids);
        queryWrapper1.eq("del_flag", FileDelFlagEnums.RECYCLE.getFlag());
        List<EFile> sel_fileList = this.baseMapper.selectList(queryWrapper1);

        // 找到子目录文件
        List<String> delFilesSubFolderFileIdList = new ArrayList<>();
        for (EFile eFile : sel_fileList){
            if (FileFolderTypeEnums.FOLDER.getType().equals(eFile.getFolderType())){
                findAllSubFolderFileList(delFilesSubFolderFileIdList,userId,eFile.getFileId(),FileDelFlagEnums.DEL.getFlag());
            }
        }

        System.out.println("找到的子文件");
        for (int i = 0; i < delFilesSubFolderFileIdList.size(); i++) {
            System.out.println(delFilesSubFolderFileIdList.get(i));
        }


        // 查找所有更目录文件
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("user_id", userId);
        queryWrapper2.in("file_pid", "0");
        queryWrapper2.eq("del_flag", FileDelFlagEnums.USING.getFlag());
        List<EFile> root_fileList = this.baseMapper.selectList(queryWrapper2);
        Map<String, EFile> rootFileMap = root_fileList.stream().collect(Collectors.toMap(EFile::getFileName, Function.identity(), (data1, data2) -> data2));

        // 查询所有所选文件 将目录下的所有文件更新为使用中
        if (!delFilesSubFolderFileIdList.isEmpty()){
            EFile eFile = new EFile();
            eFile.setDelFlag(FileDelFlagEnums.USING.getFlag());
            QueryWrapper queryWrapper3 = new QueryWrapper();
            queryWrapper3.eq("user_id", userId);
            queryWrapper3.eq("del_flag", FileDelFlagEnums.DEL.getFlag());
            queryWrapper3.in("file_pid", delFilesSubFolderFileIdList);
            int update = this.baseMapper.update(eFile, queryWrapper3);
        }

        // 将所选中的文件更新为正常，且设置为根文件
        EFile update_eFile = new EFile();
        update_eFile.setDelFlag(FileDelFlagEnums.USING.getFlag());
        update_eFile.setFilePid("0");
        QueryWrapper queryWrapper4 = new QueryWrapper();
        queryWrapper4.in("file_id", ids);
        queryWrapper4.eq("user_id", userId);
        queryWrapper4.eq("del_flag", FileDelFlagEnums.RECYCLE.getFlag());
        int update = this.baseMapper.update(update_eFile, queryWrapper4);

        // 将选中文件从回收站中移除

        QueryWrapper queryWrapper5 = new QueryWrapper();
        queryWrapper5.eq("user_id", userId);
        queryWrapper5.in("file_id", ids);
        fileRecycleService.remove(queryWrapper5);

        // 将所选文件重新命名
        for (EFile eFile : sel_fileList) {
            EFile rootFileInfo = rootFileMap.get(eFile.getFileName());
            // 文件已存在，重命名被还原的文件
            if (rootFileInfo != null){
                String fileName = eFile.getFileName() + DateUtil.today()+ (UUID.randomUUID()).toString().substring(0, 5);
                EFile tempFile = new EFile();
                tempFile.setFileName(fileName);
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("user_id", userId);
                queryWrapper.eq("file_id", eFile.getFileId());
                this.baseMapper.update(tempFile, queryWrapper);
            }
        }



    }

    private void findAllSubFolderFileList(List<String> fileIdList, String userId, String fileId, Integer delFlag){
        fileIdList.add(fileId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("file_pid", fileId);
        queryWrapper.eq("folder_type", 1);
        queryWrapper.eq("del_flag", delFlag);
        List<EFile> sel_list = this.baseMapper.selectList(queryWrapper);
        for (EFile eFile : sel_list){
            findAllSubFolderFileList(fileIdList, userId, eFile.getFileId(), delFlag);
        }

    }

    @Override
    public EFile newFolder(HttpServletRequest request, EFile eFile) {
        if (request == null || eFile == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        eFile.setUserId(userId);

        if (fileNameIsRepeat(eFile.getFilePid(), eFile.getUserId(), eFile.getFileName(), eFile.getFolderType())){
            // 文件名重复  重命名
            eFile.setFileName(eFile.getFileName() + DateUtil.today()+ (UUID.randomUUID()).toString().substring(0, 5));
        }

        eFile.setIsDeleted(0);
        eFile.setStatus(2);

        //保存数据
        this.baseMapper.insert(eFile);
        return eFile;

    }

    @Override
    public List<EFile> getFileNav(HttpServletRequest request, String path) {

        if (request == null || !StringUtils.hasLength(path)){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        String[] pathArray = path.split("/");
        EFile eFile = new EFile();
        eFile.setUserId(userId);
        eFile.setFolderType(1);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("file_id", pathArray);
        queryWrapper.orderByDesc("file_id");

        return null;
    }

    @Override
    public List<FolderTreeDto> getFolderTree(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", "1696804142155706370");
        queryWrapper.eq("folder_type", 1);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("update_time");
        List<EFile> folder_list = this.baseMapper.selectList(queryWrapper);  // 找到用户所有文件夹

        // 数据脱敏
        List<FolderTreeDto> all_list = new ArrayList<>();
        for (EFile eFile : folder_list){
            FolderTreeDto folderTreeDto = new FolderTreeDto();
            BeanUtil.copyProperties(eFile, folderTreeDto, false);
            all_list.add(folderTreeDto);
        }

        // 生成目录树
        List<FolderTreeDto> res_tree = new ArrayList<>();
        for (FolderTreeDto folderTreeDto : all_list){
            if ("0".equals(folderTreeDto.getFilePid())){
               // res_tree.add(setFolderChild(eFile,folder_list));
                res_tree.add(setFolderChild(folderTreeDto, all_list));
            }

        }
        return res_tree;
    }

    @Override
    public EFile fileRename(HttpServletRequest request, EFile eFile) {
        if (request == null){
            throw new BusinessException(EventCode.NULL_ERROR);
        }

        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }
        eFile.setUserId(userId);

        // 根据用户名和文件id查询文件
        EFile sel_file = this.baseMapper.selectOne(
                new QueryWrapper<EFile>().eq("file_id", eFile.getFileId())
                        .eq("user_id", eFile.getUserId()));
        // 文件不存在
        if (null == sel_file){
            throw new BusinessException(EventCode.FILE_NOT_EXIST);
        }

        // 文件名与原本文件名一致  直接返回
        if (sel_file.getFileName().equals(eFile.getFileName())){
            return sel_file;
        }

        if (eFile.getFolderType() == 0){
            // 文件修改了后缀 文件类型可能会变化
            FileTypeEnums fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(FileNameUtil.extName(eFile.getFileName()));
            eFile.setFileCategory(fileTypeEnums.getCategory().getCategory());
            // file_type 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:zip 9:其他
            eFile.setFileType(fileTypeEnums.getType());
        }

        boolean isRepeat = fileNameIsRepeat(eFile.getFilePid(), userId, eFile.getFileName(), eFile.getFolderType());
        if (isRepeat){  // 文件名重复
            throw new BusinessException(EventCode.FILE_ALREADY_EXIST,"文件或文件夹已存在，请重新命名");
        }

        int i = this.baseMapper.updateById(eFile);
        return eFile;
    }

    @Override
    public String createDownloadCode(HttpServletRequest request, String fileId) {
        // 获取用户id
        String userId = JwtUtils.getUserIdByJwtToken(request);
        if (userId == null){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 查询文件是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("file_id", fileId);
        EFile sel_file = this.baseMapper.selectOne(queryWrapper);
        if (null == sel_file){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 如果是目录，则不能下载
        if (sel_file.getFolderType().equals(FileFolderTypeEnums.FOLDER)){
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

        // 生成code 使用50位随机字符串作为code,避免重复
        String code = RandomUtil.randomString(60);

        //将这个code信息放入redis中临时存储,并且传回前端(使用dto对象),需要下载文件的时候将code传到后端进行判断
        DownloadFileDto downloadFileDto = new DownloadFileDto();
        downloadFileDto.setFileId(fileId);
        downloadFileDto.setFileName(sel_file.getFileName());
        downloadFileDto.setFilePath(sel_file.getFilePath());
        downloadFileDto.setDownloadCode(code);

        //有效时间为3分钟时间
        redisTemplate.opsForValue().set(MConstant.REDIS_DOWNLOAD_KEY+code, JSONUtil.toJsonStr(downloadFileDto), 3, TimeUnit.MINUTES);

        return code;
    }



    public String fileRename(String filePid, String userId, String fileName){

        QueryWrapper<EFile> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("file_name", fileName);
        wrapper.eq("file_pid", filePid);
        wrapper.eq("is_deleted", 0);

        Long count = this.baseMapper.selectCount(wrapper);
        if (count > 0){
            // 重名名
            fileName = FileNameUtil.mainName(fileName) + DateUtil.today() + (UUID.randomUUID()).toString().substring(0, 5) + FileNameUtil.extName(fileName);
        } 
        return fileName;
    }


    // 从redis中取出分片大小
    public Long getRedisChunkSize(String userId, String taskId){
        Object size = redisTemplate.opsForValue().get(userId + taskId);
        if(size == null){
            return 0L;
        }
        if (size instanceof Integer){
            return ((Integer) size).longValue();
        }else if (size instanceof Long){
            return (Long) size;
        }

        return 0L;

    }

    // 判断文件名是否重复
    public boolean fileNameIsRepeat(String filePid, String userId, String fileName, Integer folderType){

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("file_pid", filePid);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("folder_type", folderType);
        queryWrapper.eq("is_deleted", 0);
        Long count = this.baseMapper.selectCount(queryWrapper);
        return count > 0;

    }

    // 设置子目录
    private FolderTreeDto setFolderChild(FolderTreeDto folderTreeDto, List<FolderTreeDto> eFileList){
        folderTreeDto.setChildList(new ArrayList<FolderTreeDto>());

        // 遍历所有菜单List集合，进行判断比较，比较id和pid值是否相同
        for (FolderTreeDto temp_eFile : eFileList){
            // 判断 fileId 和 filePid 是否相同
            if(folderTreeDto.getFileId().equals(temp_eFile.getFilePid())){

                // 如果children为空，进行初始化操作
                if (folderTreeDto.getChildList() == null){
                    folderTreeDto.setChildList(new ArrayList<FolderTreeDto>());
                }
                // 把查询出来的子目录放到一级目录里
                folderTreeDto.getChildList().add(setFolderChild(temp_eFile, eFileList));
            }
        }
        return folderTreeDto;
    }
//    private EFile setFolderChild(EFile eFile, List<EFile> eFileList){
//        eFile.setChildList(new ArrayList<EFile>());
//
//        // 遍历所有菜单List集合，进行判断比较，比较id和pid值是否相同
//        for (EFile temp_eFile : eFileList){
//            // 判断 fileId 和 filePid 是否相同
//            if(eFile.getFileId().equals(temp_eFile.getFilePid())){
//
//                // 如果children为空，进行初始化操作
//                if (eFile.getChildList() == null){
//                    eFile.setChildList(new ArrayList<EFile>());
//                }
//                // 把查询出来的子目录放到一级目录里
//                eFile.getChildList().add(setFolderChild(temp_eFile, eFileList));
//            }
//        }
//        return eFile;
//    }

}




