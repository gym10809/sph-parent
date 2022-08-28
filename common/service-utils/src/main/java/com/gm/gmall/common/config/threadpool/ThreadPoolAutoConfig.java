package com.gm.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gym
 * @create 2022/8/28 0028 19:32
 */
@EnableConfigurationProperties(ThreadPoolProperties.class)
@Configuration
public class ThreadPoolAutoConfig {
    @Autowired
    ThreadPoolProperties poolProperties;
    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public ThreadPoolExecutor getExecutor(){
        ThreadPoolExecutor executor=new ThreadPoolExecutor(
                poolProperties.getCore(), poolProperties.getMax(), poolProperties.getKeepAliveTime(), TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(poolProperties.getQueueSize()),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        int i=0;
                        Thread thread=new Thread(r);
                        thread.setName(applicationName+"-thread:"+i++);
                        return thread;
                    }
                },new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return executor;
    }
}
