package com.gm.gmall.search;

import com.gm.gmall.common.annataion.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author gym
 * @create 2022/9/3 0003 18:26
 */
@SpringCloudApplication
@EnableElasticsearchRepositories
@EnableThreadPool
public class SearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class,args);
    }
}
