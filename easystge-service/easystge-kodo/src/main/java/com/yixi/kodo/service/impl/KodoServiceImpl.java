package com.yixi.kodo.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.qiniu.storage.*;
import com.qiniu.util.Auth;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.utils.EventCode;
import com.yixi.kodo.model.vo.UploadFileVo;
import com.yixi.kodo.service.KodoService;
import com.yixi.kodo.utils.QiNiuProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author yixi
 * @date 2023/8/24
 * @apiNote
 */
@Service
public class KodoServiceImpl implements KodoService {
    @Override
    public String uploadFile(MultipartFile file) {
        String filename = null;
        try {
            //1、获取文件上传的流
            byte[] fileBytes = file.getBytes();
            //2、创建日期目录分隔
            String datePath  = DateUtil.format(new Date(), "yyyy/MM/dd");
            //3、获取文件名
            String originalFilename = file.getOriginalFilename();
           // String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            filename = datePath+"/"+ UUID.randomUUID().toString().replaceAll("-", "")+ originalFilename;

            //4.构造一个带指定 Region 对象的配置类
            //Region.huabei(根据自己的对象空间的地址选
            Configuration cfg = new Configuration(Region.huanan());
            UploadManager uploadManager = new UploadManager(cfg);
            //5.获取七牛云提供的 token
            Auth auth = Auth.create(QiNiuProperties.ACCESS_KEY, QiNiuProperties.SECRET_KEY);
            String upToken = auth.uploadToken(QiNiuProperties.BUCKET);
            uploadManager.put(fileBytes,filename,upToken);

            return QiNiuProperties.PATH+filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

    }

    @Override
    public String uploadChunk(Map<String, Object> fileInfo, MultipartFile file) {
        String filename = null;
        try {
            //1、获取文件上传的流
            byte[] fileBytes = file.getBytes();
            //2、创建日期目录分隔
            String datePath  = DateUtil.format(new Date(), "yyyy/MM/dd");
            //3、获取文件名
            String tempFolder = (String) fileInfo.get("tempFolder"); // 存储文件分片的临时目录
            filename = tempFolder+"/"+fileInfo.get("chunkIndex");

            //4.构造一个带指定 Region 对象的配置类
            //Region.huabei(根据自己的对象空间的地址选
            Configuration cfg = new Configuration(Region.huanan());
            UploadManager uploadManager = new UploadManager(cfg);
            //5.获取七牛云提供的 token
            Auth auth = Auth.create(QiNiuProperties.ACCESS_KEY, QiNiuProperties.SECRET_KEY);
            String upToken = auth.uploadToken(QiNiuProperties.BUCKET);
            uploadManager.put(fileBytes,filename,upToken);


            return QiNiuProperties.PATH+filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(EventCode.PARAMS_ERROR);
        }

//        String filename = null;
//        try {
//            //1、获取文件上传的流
//            byte[] fileBytes = uploadFileVo.getFile().getBytes();
//            //2、创建日期目录分隔
//            String datePath  = DateUtil.format(new Date(), "yyyy/MM/dd");
//            //3、获取文件名
//            String tempFolder = uploadFileVo.getFileId()+uploadFileVo.getMd5(); // 存储文件分片的临时目录
//            filename = uploadFileVo.getFileId()+uploadFileVo.getMd5()+"/"+uploadFileVo.getChunkIndex();
//            //4.构造一个带指定 Region 对象的配置类
//            //Region.huabei(根据自己的对象空间的地址选
//            Configuration cfg = new Configuration(Region.huanan());
//            UploadManager uploadManager = new UploadManager(cfg);
//            //5.获取七牛云提供的 token
//            Auth auth = Auth.create(QiNiuProperties.ACCESS_KEY, QiNiuProperties.SECRET_KEY);
//            String upToken = auth.uploadToken(QiNiuProperties.BUCKET);
//            uploadManager.put(fileBytes,filename,upToken);
//            return QiNiuProperties.PATH+filename;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new BusinessException(EventCode.PARAMS_ERROR);
//        }
    }

    public static void main(String[] args) {
        File dir = new File("http://rzu71bqgk.hn-bkt.clouddn.com/173ea820-ecbe-48d4-b327-bbfe0ef977f433adbf59a9ef50d38cbce7d859aa863d/0");
        System.out.println();
        System.out.println("dir is "+dir);

    }
}
