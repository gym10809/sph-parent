package com.gm.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author gym
 * @create 2022/9/8 0008 8:49
 */
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.gm.gmall.common.feignClient.product"})
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}
