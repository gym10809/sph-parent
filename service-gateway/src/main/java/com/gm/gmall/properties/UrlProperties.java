package com.gm.gmall.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/7 0007 11:09
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.auth")
public class UrlProperties {

     private List<String>  noAuthUrl;
     private String loginPage;
     private List<String> loginUrl;
     private List<String> rejectUrl;
}
