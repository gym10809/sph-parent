package com.gm.gmall.common.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gym
 * @create 2022/8/28 0028 19:32
 */
@Data
@ConfigurationProperties(prefix = "thread.param")
public class ThreadPoolProperties {
   private Integer core = 2;
   private Integer max = 4;
   private Integer queueSize = 200;
   private Long keepAliveTime = 300L;

}
