package com.gm.gmall.product.controller;


import com.gm.gmall.common.result.Result;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;


/**
 * @author gym
 * @create 2022/8/23 0023 21:35
 */
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Value("${minio.endpoint}")
    private String endpoint; //
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.bucketName}")
    private String bucketName;
    /**
     * 头像上传至minio
     * @param file
     * @return
     */
    @PostMapping("/fileUpload")
    public Result  fileUpload(@RequestPart("file")MultipartFile file) throws Exception{
        // // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        //  // 检查存储桶是否已经存在
        //      boolean isExist = minioClient.bucketExists("asiatrip");
        //      if(isExist) {
        //        System.out.println("Bucket already exists.");
        //      } else {
        //        // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
        //        minioClient.makeBucket("asiatrip");
        //      }
        //                        .build());
        boolean b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!b){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        // 使用putObject上传一个文件到存储桶中
        // minioClient.putObject(
        //                PutObjectArgs.builder().bucket("my-bucketname").object("my-objectname").stream(
        //                                bais, bais.available(), -1)
        InputStream inputStream = file.getInputStream();
        String objectName= new Date().toString()+ UUID.randomUUID();//文件名
        //上传文件
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                inputStream, file.getSize(),-1).contentType(file.getContentType())
                        .build());
            String url=endpoint+"/"+bucketName+"/"+objectName;
        System.out.println(url);
        return Result.ok(url);
    }
}
