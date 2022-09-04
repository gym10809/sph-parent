package com.gm.gmall.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * @author gym
 * @create 2022/9/1 0001 18:38
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheSkuInfo {
    String bloomName() default "";//设置对应的布隆过滤器的名字

    String redisName() default "";//需要查询的redis名字
    String lockName() default ""; //锁的名字
    String bloomVal() default ""; //查询的布隆的值

}
