package com.yixi.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.yixi.oss.utils.OSSUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import javax.annotation.Resource;
import java.io.*;

/**
 * @author yixi
 * @date 2023/8/30
 * @apiNote
 */
@SpringBootTest
public class TestDemo {
    @Resource
    private OSSUtil ossUtil;

    @Test
    public void test1() throws IOException {
        String s = ossUtil.dumpThumbnail("", "", "2023/08/30/c9a03d0d-3faf-4989-b286-fae61b416bd8b39b5f53263837d21a9a13ff1e274074.mp4");
    }


    @Test
    public void test2() throws Exception {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-guangzhou.aliyuncs.com";
        String accessKeyId = "LTAI5t5dbeG4hectB2UVeTyr";
        String accessKeySecret = "XBuS0friqEOZ9Z6cNKrmnsL3Y88xdt";

        //从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 填写Bucket名称，例如examplebucket。
        String bucketName = "easystge";
        // 填写不包含Bucket名称在内的Object完整路径，例如testfolder/exampleobject.txt。

        String objectName = "2023/08/30/32fd2e77-7024-4f1d-991f-bfcb1f3b1b00072akioni.jpeg";
        String pathName = "hh.jpeg";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            boolean b = ossClient.doesObjectExist(bucketName, objectName);
            System.out.println("b is == "+b);

            OSSObject object = ossClient.getObject(new GetObjectRequest(bucketName, objectName));
            InputStream objectContent = object.getObjectContent();
            FileOutputStream downloadFile = new FileOutputStream(pathName);
            byte[] bytes = new byte[1024];
            int index;
            while((index = objectContent.read(bytes))!= -1){
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;

                System.out.println("\n" + line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            reader.close();
            // ossObject对象使用完毕后必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            object.close();
            System.out.println("objectContent is "+ objectContent);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
