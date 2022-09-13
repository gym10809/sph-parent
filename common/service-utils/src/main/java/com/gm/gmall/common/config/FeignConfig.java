package com.gm.gmall.common.config;

import com.gm.gmall.common.constant.RedisConstant;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gym
 * @create 2022/9/13 0013 20:55
 */
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor userRequestInterceptor(){
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
