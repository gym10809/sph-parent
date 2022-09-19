package com.gm.gmall.pay;

import com.gm.gmall.common.annataion.EnableException;
import com.gm.gmall.common.annataion.EnableFeignInterceptor;
import com.gm.gmall.rabbit.annotation.EnableRabbitMqTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author gym
 * @create 2022/9/16 0016 13:46
 */
@SpringCloudApplication
@EnableFeignInterceptor
@EnableFeignClients(value = "com.gm.gmall.common.feignClient.order")
@EnableException
@EnableRabbitMqTemplate
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }
}
