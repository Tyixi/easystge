package com.yixi.oss.utils;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Formatter;
import java.util.List;
import java.util.UUID;

/**
 * @author yixi
 * @date 2023/8/28
 * @apiNote
 */
@Slf4j
@Component
public class OSSUtil {
    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.bucketNamePublic}")
    private String bucketNamePublic;

    private OSS ossClient;

    @PostConstruct
    public void init(){
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);
    }

    /**
     * 分块上传完成获取结果
     */
    public String completerPartUploadFile(String fileKey, String uploadId, List<PartETag> partETags){
        String url = null;
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组成一个完整的文件
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, fileKey, uploadId, partETags);

        ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        // String downloadUrl = getDownloadUrl(fileKey, bucketName);
        return fileKey;
    }

    /**
     * 文件分片上传
     * @param fileKey   文件名
     * @param in    文件数据流
     * @param uploadId  oss唯一分片id
     * @param fileMd5   文件MD5
     * @param partNum   第几分片
     * @param partSize  分片大小
     * @return
     */
    public PartETag partUploadFile(String fileKey, InputStream in, String uploadId, String fileMd5, int partNum, long partSize){
        System.out.println("文件流 is "+in);
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setPartNumber(partNum);
        uploadPartRequest.setPartSize(partSize);
        uploadPartRequest.setInputStream(in);
        uploadPartRequest.setKey(fileKey);
//        uploadPartRequest.setMd5Digest(fileMd5);
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
        return uploadPartResult.getPartETag();
    }


    /**
     * 分片上传完成获取结果
     * @param fileKey
     * @return
     */
    public String getUploadId(String fileKey){
        String uploadId = null;
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, fileKey);

        // 初始化分片
        InitiateMultipartUploadResult unrest = ossClient.initiateMultipartUpload(initiateMultipartUploadRequest);

        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个ID来发起相关的操作，如取消分片上传，查询分片上传等
        uploadId = unrest.getUploadId();
        return uploadId;

    }

    /**
     * 获取文件下载链接
     * @param pathFile 首字母不带 / 的路径和文件
     * @param bucketName
     * @return  成功返回地址 否则返回null
     */
    public String getDownloadUrl(String pathFile, String bucketName){
        if (bucketName == null || "".equals(bucketName)){
            bucketName = bucketName;
        }

        StringBuffer url = new StringBuffer();
        url.append("http://").append(bucketName).append(endpoint).append("/");

        if (pathFile != null && !"".equals(pathFile)){
            url.append(pathFile);
        }
        return url.toString();
    }

    /**
     * 上传文件到阿里云，并生成url
     *
     * @param fileDir   (key)文件名 (不包括后缀)
     * @param in    文件字节流
     * @param fileName
     * @param isRandomName
     * @return  String 生成的文件url
     */
    public String uploadFile(String fileDir, InputStream in, String fileName, boolean isRandomName){
        String url = null;
        String suffix= fileName.substring(fileName.lastIndexOf(".") + 1);
        if (isRandomName){
            fileName = UUID.randomUUID().toString().replace("_", "")+"."+suffix;
        }

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建上传Object 的 Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(in.available());
            // 指定该object的网页缓存行为，表示用户指定的HTTP请求/回复链的缓存行为不经过本地缓存
            objectMetadata.setCacheControl("no-Cache");
            // 设置页面不缓存
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(suffix));
            // 指定该Object被下载时的名称
            objectMetadata.setContentDisposition("inline;filename="+fileName);

            // 上传文件
            ossClient.putObject(bucketName, fileDir+"/"+fileName, in,objectMetadata);

            url = buildUrl(fileDir+ "/" + fileName);


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ossClient.shutdown();

                try {
                    if (in != null){
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return url;

    }


    private String buildUrl(String fileDir){
        StringBuffer url = new StringBuffer();
        if (!StringUtils.hasLength(bucketName)){
            log.error("bucketName为空");
            return null;
        }
        if (!StringUtils.hasLength(endpoint)){
            log.error("endpoint为空");
            return null;
        }

        if (!StringUtils.hasLength(fileDir)){
            log.error("上传文件目录为空");
            return null;
        }

        url.append("https://").append(bucketName).append(".").append(endpoint).append("/").append(fileDir);
        return url.toString();
    }

    /**
     * 删除图片
     * @param key
     */
    public void deletePicture(String key){
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName,key);
        ossClient.shutdown();
    }

    /**
     * 判断OSS服务文件上传时文件的contentType
     * @param suffix
     * @return
     */
    public String getContentType(String suffix){
        if (suffix.equalsIgnoreCase("bmp")){
            return "image/bmp";
        }else if (suffix.equalsIgnoreCase("gif")){
            return "image/gif";
        }else if (suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("jpg")){
            return "image/jpeg";
        }else if (suffix.equalsIgnoreCase("png")){
            return "image/png";
        }else if (suffix.equalsIgnoreCase("html")){
            return "text/html";
        }else if (suffix.equalsIgnoreCase("txt")){
            return "text/plain";
        }else if (suffix.equalsIgnoreCase("vsd")){
            return "application/vnd.visio";
        }else if (suffix.equalsIgnoreCase("pptx") || suffix.equalsIgnoreCase("ppt")){
            return "application/vnd.ms-powerpoint";
        }else if (suffix.equalsIgnoreCase("docx") || suffix.equalsIgnoreCase("doc")){
            return "application/msword";
        }else if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")){
            return "application/vnd.ms-excel";
        }else if (suffix.equalsIgnoreCase("xml")){
            return "text/xml";
        }else if (suffix.equalsIgnoreCase("mp3")){
            return "audio/mp3";
        }else if (suffix.equalsIgnoreCase("amr")){
            return "audio/amr";
        }else if (suffix.equalsIgnoreCase("pdf")){
            return "application/pdf";
        }else {
            return "text/plain";
        }

    }

    /**
     *  阿里云转储缩略图(图片格式只能是：jpg、png、bmp、gif、webp、tiff)
     * @param targetBucketName  目标bucketName（公共读，私有写）
     * @param sourceBucketName  源文件bucketName（私有读写）
     * @param sourceImage       源文件路径（需要生成缩略图的源文件路径）
     * @return 缩略图路径
     * @throws IOException
     */
    public String dumpThumbnail(String targetBucketName,String sourceBucketName,String sourceImage)throws IOException{
        // 图片处理持久化 : 缩放
        StringBuilder sbStyle = new StringBuilder();
        Formatter styleFormatter = new Formatter(sbStyle);
        String styleType = "image/resize,m_fixed,w_150,h_150";
        String targetImage = sourceImage;
        styleFormatter.format("%s|sys/saveas,o_%s,b_%s", styleType,
                BinaryUtil.toBase64String(targetImage.getBytes()),
                BinaryUtil.toBase64String(bucketNamePublic.getBytes()));
        System.out.println(sbStyle.toString());//输设置后的出样式
        ProcessObjectRequest request = new ProcessObjectRequest(bucketName, sourceImage, sbStyle.toString());
        GenericResult processResult = ossClient.processObject(request);
        String json = IOUtils.readStreamAsString(processResult.getResponse().getContent(), "UTF-8");
        processResult.getResponse().getContent().close();
        System.out.println(json);   //输出相应结果，可根据结果做出对应的操作
        return targetImage;
    }


    /**
     * 获取文件流
     * @param url
     * @return
     */
    public InputStream getInputStream(String url){
        OSSObject object = ossClient.getObject(new GetObjectRequest(bucketName, url));
        InputStream objectContent = object.getObjectContent();
        return objectContent;
    }


}
