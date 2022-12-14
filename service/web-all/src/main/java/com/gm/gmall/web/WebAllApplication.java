package com.gm.gmall.web;

import com.gm.gmall.common.annataion.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author gym
 * @create 2022/8/26 0026 9:18
 */
@SpringCloudApplication
@EnableFeignClients(basePackages = {"com.gm.gmall.common.feignClient"})
@EnableFeignInterceptor
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class,args);
    }
}
