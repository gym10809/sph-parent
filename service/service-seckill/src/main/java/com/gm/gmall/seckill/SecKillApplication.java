package com.gm.gmall.seckill;

import com.gm.gmall.common.annataion.EnableException;
import com.gm.gmall.common.annataion.EnableFeignInterceptor;
import com.gm.gmall.rabbit.annotation.EnableRabbitMqTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author gym
 * @create 2022/9/19 0019 19:24
 */
@SpringCloudApplication
@EnableFeignInterceptor
@EnableException
@EnableRabbitMqTemplate
@MapperScan("com.gm.gmall.seckill.mapper")
@EnableScheduling
@EnableFeignClients(value = {"com.gm.gmall.common.feignClient.user","com.gm.gmall.common.feignClient.order"})
public class SecKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillApplication.class,args);
    }
}
