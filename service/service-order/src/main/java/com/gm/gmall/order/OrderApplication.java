package com.gm.gmall.order;

import com.gm.gmall.common.annataion.EnableFeignInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author gym
 * @create 2022/9/11 0011 21:43
 */
@SpringCloudApplication
@MapperScan(value = "com.gm.gmall.order.mapper")
@EnableFeignClients(value = {"com.gm.gmall.common.feignClient.cart",
                                "com.gm.gmall.common.feignClient.product",
                                "com.gm.gmall.common.feignClient.user"})
@EnableFeignInterceptor
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
