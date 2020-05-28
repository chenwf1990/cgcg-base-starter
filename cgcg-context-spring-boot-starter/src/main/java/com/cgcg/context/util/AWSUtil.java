package com.cgcg.context.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 封装白山云常用操作
 *
 * @author xujinbang
 * @date 2019-3-14
 */
public class AWSUtil {
    private static String bucketName;
    private static String prefix;
    private static AmazonS3 s3;

    static {
        final FileEnvProperties fep = FileEnvProperties.getInstance();
        String endPoint = fep.getUrl();
        String accessKey = fep.getKey();
        String secretKey = fep.getSecret();
        bucketName = fep.getBucket();
        prefix = fep.getPrefix();
        //初始化
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientconfiguration = new ClientConfiguration();
        clientconfiguration.setSocketTimeout(60 * 60 * 1000);
        clientconfiguration.setConnectionTimeout(60 * 60 * 1000);
        s3 = new AmazonS3Client(awsCreds, clientconfiguration);
        s3.setEndpoint(endPoint);
        s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());
    }

    /**
     * 上传图片到BSC,并返回上传图片的URL
     *
     * @return
     */
    public static String uploadFileToAWS(InputStream is, String fileName) {

        //上传开始
        String key = prefix + "/" + fileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //设置文件类型
        objectMetadata.setContentType(getContentType(fileName));
        PutObjectRequest putObjectrequest = new PutObjectRequest(bucketName, key, is, objectMetadata);

        //您可以使用下面两种方式为上传的文件指定ACL，后一种已被注释
        putObjectrequest.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        s3.putObject(putObjectrequest);

        URL url = s3.generatePresignedUrl(bucketName, key, new Date(119, 00, 22));
        String urlString = url.toString();
        String[] splitStr = urlString.split("\\?");
        return splitStr[0];
    }

    /**
     * 根据后缀名获取图片MIME类型
     *
     * @param fileName
     * @return
     */
    private static String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension) || "png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if ("html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        return "text/html";
    }

}
