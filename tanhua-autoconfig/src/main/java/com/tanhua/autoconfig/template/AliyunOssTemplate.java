package com.tanhua.autoconfig.template;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.tanhua.autoconfig.properties.AliyunOssProperties;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @Description: aliyun Oss service 阿里云OSS服务
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
public class AliyunOssTemplate {
    private AliyunOssProperties properties;

    public AliyunOssTemplate(AliyunOssProperties properties) {
        this.properties = properties;
    }


    /**
     * 这个思路就是 uploadKey = dateMark + md5OfFileName + fileSuffix，该方法包含上传功能
     *
     * @param uploadKey
     * @param is
     * @return 返回完整地址
     */
    protected String upload(String uploadKey, InputStream is) {
        String endpoint = properties.getEndpoint();
        String accessKeyId = properties.getAccessKeyId();
        String accessKeySecret = properties.getAccesskeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(properties.getBucketName(), uploadKey, is);
        } catch (OSSException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
        String url = properties.getUrl() + uploadKey;
        return url;
    }


    /**
     * 上传文件
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String url = null;
        //need 2 pcs of stream，one for MD5，the other for upload to Oss。https://blog.csdn.net/xueyijin/article/details/121526772
        InputStream inputStream = file.getInputStream();
        //
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        InputStream inputStreamForMD5 = new ByteArrayInputStream(baos.toByteArray());
        InputStream inputStreamForOss = new ByteArrayInputStream(baos.toByteArray());
            /*其实用下面这个也是一样的，但是哪个更高效和通用？？？
            InputStream inputStreamForMD5 = file.getInputStream();
            InputStream inputStreamForOss = file.getInputStream();
             */
        //get original name
        String filename = file.getOriginalFilename();
        //get suffix
        String fileSuffix = filename.substring(file.getOriginalFilename().lastIndexOf("."));
        //获取一个md加密
        String md5OfFileName = DigestUtils.md5DigestAsHex(inputStreamForMD5);
        //这里是为了按上传时间分配目录。精确到月,这里用到一个第三方的jar包，记得笔记2022/5/18
        String dateMark = DateTime.now().toString("yyyyMMdd/");
        //拼接成完整的文件名。
        final String uploadKey = dateMark + md5OfFileName + fileSuffix;
        //upload them
        url = upload(uploadKey, inputStreamForOss);

        return url;
    }


    public String uploadByPath(String path) throws IOException {
        String url = null;
        String fileSuffix = path.substring(path.lastIndexOf(".")).toLowerCase();
        //获取一个md加密
        //将path路径转为文件
        File file = new File(path);
        FileInputStream fileInputStreamForName = new FileInputStream(file);
        FileInputStream fileInputStreamForOss = new FileInputStream(file);
        String md5OfFileName = DigestUtils.md5DigestAsHex(fileInputStreamForName);
        //这里是为了按上传时间分配目录。精确到月,这里用到一个第三方的jar包，记得笔记2022/5/18
        String dateMark = DateTime.now().toString("yyyyMMdd/");
        //拼接成完整的文件名。
        final String uploadKey = dateMark + md5OfFileName + fileSuffix;
        //upload them
        url = upload(uploadKey, fileInputStreamForOss);
        return url;
    }
}
