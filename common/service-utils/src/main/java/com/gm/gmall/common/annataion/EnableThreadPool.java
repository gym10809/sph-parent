package com.gm.gmall.common.annataion;

import com.gm.gmall.common.config.threadpool.ThreadPoolAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author gym
 * @create 2022/8/28 0028 21:49
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ThreadPoolAutoConfig.class)
public @interface EnableThreadPool {
}
