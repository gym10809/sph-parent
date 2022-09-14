package com.gm.gmall.common.annataion;

import com.gm.gmall.common.config.FeignConfig;
import com.gm.gmall.common.execption.GmallException;
import com.gm.gmall.common.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author gym
 * @create 2022/9/14 0014 8:28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = GlobalExceptionHandler.class)
public @interface EnableException {
}
