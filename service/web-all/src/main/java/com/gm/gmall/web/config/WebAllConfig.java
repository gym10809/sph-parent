package com.gm.gmall.web.config;

import com.gm.gmall.common.constant.RedisConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gym
 * @create 2022/9/8 0008 15:46
 */
@Configuration
public class WebAllConfig {

    @Bean
    public RequestInterceptor userRequsetInterceptor(){

        return (template -> {
           //获取老请求中的
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String userId = request.getHeader(RedisConstant.USERID);
            String tempId = request.getHeader(RedisConstant.TEMPID);
            template.header(RedisConstant.TEMPID,tempId);
            template.header(RedisConstant.USERID,userId);
        });

    }
}
