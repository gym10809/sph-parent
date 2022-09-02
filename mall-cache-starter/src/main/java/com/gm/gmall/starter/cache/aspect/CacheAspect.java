package com.gm.gmall.starter.cache.aspect;


import com.gm.gmall.starter.cache.annotation.CacheSkuInfo;
import com.gm.gmall.starter.cache.config.constant.RedisConstant;
import com.gm.gmall.starter.cache.service.CacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;



/**
 * @author gym
 * @create 2022/9/1 0001 18:36
 */
@Aspect //是一个切面
@Component
public class CacheAspect {
    @Autowired
    CacheService cacheService;
    ExpressionParser parser = new SpelExpressionParser();
    ParserContext context=new TemplateParserContext();

    @Around("@annotation(com.gm.gmall.starter.cache.annotation.CacheSkuInfo)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取对应注解上的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //获取方法的形参
        Object[] args = joinPoint.getArgs();
        //获取方法的返回值类型
        Type returnType = method.getGenericReturnType();
        //获取方法上面的注解对应的值
        CacheSkuInfo annotation = method.getDeclaredAnnotation(CacheSkuInfo.class);
        //解析成对应需要查询的缓存的id
        String bloomName = annotation.bloomName();
        //查询对应的数据
        String skuInfo =getSkuInfo(joinPoint) ;
        Object skuInfoDetailTo = cacheService.getCache(skuInfo, returnType);
        if (skuInfoDetailTo == null){
            //查询布隆过滤器是否数据库有此id的值
            //获取需要布隆过滤器判定的值
            String val = (String) getValue(joinPoint);
            int i = Integer.parseInt(val);
            boolean contains=  cacheService.bloomContains(bloomName,i);
          //如果为空，则没有
            if (!contains){
                return null;
            }
            //如果有，准备回源
            //获取对应的锁
            boolean ifLock = false;
            String lock="";
            try {
                lock=getLock(joinPoint);
                ifLock =  cacheService.tryLock(lock);
                if (ifLock){
                    //获取到锁，回源查询数据
                    skuInfoDetailTo = joinPoint.proceed(args);
                    //存缓存
                    cacheService.saveDetail(skuInfo,skuInfoDetailTo);
                    return skuInfoDetailTo;
                }else {
                    Thread.sleep(1000);
                    return cacheService.getCache(skuInfo,returnType);
                }
            } finally {
                if (ifLock) {
                    cacheService.unLock(lock);
                }
            }
        }

        return skuInfoDetailTo;
    }

//    private String getRelInfoName(ProceedingJoinPoint joinPoint) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        CacheSkuInfo annotation = method.getAnnotation(CacheSkuInfo.class);
//
//        return null;
//    }

    /**
     * 获取对应的锁名
     * @param joinPoint
     * @return
     */
    private String getLock(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheSkuInfo annotation = method.getAnnotation(CacheSkuInfo.class);
        String lockName = annotation.lockName();
        if (StringUtils.isEmpty(lockName)){
            lockName= RedisConstant.LOCK_PFE+method.getName();//如果没有指定，则使用方法名
        }
        evaluationExpression(lockName,joinPoint,String.class);
        return lockName;
    }

    /**
     * 得到查询缓存的对应id
     * @param joinPoint
     * @return
     */
    private String getSkuInfo(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //2、拿到注解
       CacheSkuInfo  cacheAnnotation = method.getDeclaredAnnotation(CacheSkuInfo.class);
        String expression = cacheAnnotation.skuInfo();
        //3、根据表达式计算缓存键
        String cacheKey = evaluationExpression(expression,joinPoint,String.class);
        return cacheKey;
    }

    /**
     * 解析注解表达式
     * @param expression
     * @param joinPoint
     * @param stringClass
     * @return
     */
    private String evaluationExpression(String expression, ProceedingJoinPoint joinPoint, Class<String> stringClass) {
        Expression parseExpression = parser.parseExpression(expression, context);
        //获取计算上下文
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        //将形参列表传递进参数上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params",args);
        //解析注解表达式
        String value = parseExpression.getValue(evaluationContext, stringClass);
        return value;
    }

    /**
     * 得到查询布隆过滤器的id
     * @param joinPoint
     * @return
     */
    private Object getValue(ProceedingJoinPoint joinPoint) {
        //获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheSkuInfo annotation = method.getAnnotation(CacheSkuInfo.class);
        //拿到表达式
        String bloomVal = annotation.bloomVal();
        String s = evaluationExpression(bloomVal, joinPoint, String.class);
        return s;
    }
}
