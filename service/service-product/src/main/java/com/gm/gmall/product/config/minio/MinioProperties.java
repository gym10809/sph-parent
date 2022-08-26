package com.gm.gmall.product.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gym
 * @create 2022/8/25 0025 10:58
 */
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {
    String endpoint;
    String accessKey;
    String secretKey;
    String bucketName;
}
