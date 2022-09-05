package com.gm.gmall.product;

import com.gm.gmall.common.config.RedissonAutoConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author gym
 * @create 2022/8/22 0022 20:16
 */
@MapperScan("com.gm.gmall.product.mapper")
@SpringCloudApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.gm.gmall.common.feignClient.search")
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
