package com.gm.gmall.product.config.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gym
 * @create 2022/8/25 0025 10:58
 */

@Configuration
public class MinioAutoConfig {
    @Autowired
    MinioProperties minioProperties;
    @Bean
    public MinioClient minioClient() throws Exception{
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioProperties.getEndpoint())
                        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                        .build();
        boolean b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        if (!b){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
        }
        return minioClient;
    }
}
