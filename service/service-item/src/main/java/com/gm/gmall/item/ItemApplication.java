package com.gm.gmall.item;

import com.gm.gmall.common.annataion.EnableThreadPool;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


/**
 * @author gym
 * @create 2022/8/26 0026 21:18
 */
@SpringCloudApplication
@EnableFeignClients
@EnableThreadPool
@EnableAspectJAutoProxy
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class,args);
    }
}
