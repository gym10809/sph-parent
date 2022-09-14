package com.gm.gmall.rabbit.annotation;

import com.gm.gmall.rabbit.config.RabbitMqAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author gym
 * @create 2022/9/14 0014 18:29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RabbitMqAutoConfig.class)
public @interface EnableRabbitMqTemplate {
}
