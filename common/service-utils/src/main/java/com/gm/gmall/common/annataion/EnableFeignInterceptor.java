package com.gm.gmall.common.annataion;

import com.gm.gmall.common.config.FeignConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author gym
 * @create 2022/9/13 0013 21:06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = FeignConfig.class)
public @interface EnableFeignInterceptor {
}
