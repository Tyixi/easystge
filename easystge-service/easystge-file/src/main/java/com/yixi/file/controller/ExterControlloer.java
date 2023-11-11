package com.yixi.file.controller;

import cn.hutool.json.JSONUtil;
import com.yixi.common.constants.MConstant;
import com.yixi.common.constants.UserConstant;
import com.yixi.common.exception.BusinessException;
import com.yixi.common.user.UserInfo;
import com.yixi.common.utils.BaseResponse;
import com.yixi.common.utils.EventCode;
import com.yixi.common.utils.ResultUtils;
import com.yixi.file.client.OssServiceClient;
import com.yixi.file.model.dto.DownloadFileDto;
import com.yixi.file.model.entity.EFile;
import com.yixi.file.service.EFileService;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author yixi
 * @date 2023/10/9
 * @apiNote
 */
@RestController
@RequestMapping("/fileService/exter")
public class ExterControlloer {
    private final EFileService eFileService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OssServiceClient ossServiceClient;
    public ExterControlloer(EFileService eFileService,RedisTemplate redisTemplate,OssServiceClient ossServiceClient,RabbitTemplate rabbitTemplate){
        this.eFileService = eFileService;
        this.redisTemplate = redisTemplate;
        this.ossServiceClient = ossServiceClient;
    }

    /**
     * 创建下载链接返回code
     * @param request
     * @param fileId
     * @return
     */
    @GetMapping("/downloadCode/{fileId}")
    public BaseResponse createDownloadCode(HttpServletRequest request, @PathVariable("fileId") String fileId){
        System.out.println("downloadCode is "+fileId);
        String downloadCode = eFileService.createDownloadCode(request, fileId);
        return ResultUtils.success(downloadCode);
    }

    /**
     * 下载文件
     * @param request
     * @param downloadCode
     */
    @GetMapping("/downFile/{downloadCode}")
    public void downFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("downloadCode") String downloadCode){
        System.out.println("downFile is "+downloadCode);
        if (!StringUtils.hasLength(downloadCode)){
            throw new BusinessException(EventCode.NULL_ERROR);
        }
        String downloadFileJson = (String)(redisTemplate.opsForValue().get(MConstant.REDIS_DOWNLOAD_KEY + downloadCode));
        if(null == downloadFileJson){
            return;
        }
        DownloadFileDto downloadFile = JSONUtil.toBean(downloadFileJson, DownloadFileDto.class);

        InputStream inputStream = null;
        try {
            Response ossServiceResponse = ossServiceClient.getFileInputStream(downloadFile.getFilePath());
            if (ossServiceResponse.status() == 500){
                throw new BusinessException(EventCode.PARAMS_ERROR);
            }
            Response.Body body = ossServiceResponse.body();
            inputStream = body.asInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            //设置响应
            response.setContentType("application/octet-stream;charset=UTF-8");
            // 将响应头中的Content-Disposition暴露出来，不然前端获取不到
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//            response.setHeader("Content-Disposition", ossServiceResponse.headers().get("Content-Disposition").toString().replace("[","").replace("]",""));
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(downloadFile.getFileName(), "UTF-8"));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            int length = 0;
            byte[] temp = new byte[1024 * 10];
            while ((length = bufferedInputStream.read(temp)) != -1) {
                bufferedOutputStream.write(temp, 0, length);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @ApiOperation("获取当前目录路径")
    @GetMapping("/filePath")
    public BaseResponse filePath(HttpServletRequest request, String path){
        //将传递过来的path路径进行分割
        String[] pathArr = path.split("/");
        List<EFile> eFiles = eFileService.filePath(pathArr);
        return ResultUtils.success(eFiles);
    }

}
