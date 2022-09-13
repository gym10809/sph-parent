package com.gm.gmall.login;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gym
 * @create 2022/9/6 0006 20:07
 */
@SpringCloudApplication
@MapperScan("com.gm.gmall.login.mapper")
@EnableFeignClients
public class LoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class,args);
    }
}
