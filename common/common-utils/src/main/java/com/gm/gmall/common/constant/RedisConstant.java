package com.gm.gmall.common.constant;

/**
 * @author gym
 * @create 2022/8/30 0030 16:09
 */

public class RedisConstant {
    public static final String USERID = "userId";
    public static final String TEMPID = "userTempId";
    public static final String PRE_CART = "user:cart:";
    public static final long MAX_SIZE = 200;
    public static final int MAX_NUM = 99;
    public static final String ORDER_TEMP_TOKEN = "order:token:";
    public static final int ORDER_EXPIRE = 60 * 45*1000;
    public static final String MQ_TEMP = "rabbit:temp:";
    public static String LOCK_PFE="lock:skuInfo:detail:";
    public static String SKU_INFO_PRE="skuInfo:detail:";
    public static String BLOOM_PRE="bloom:skuId";
    public static final String LOGIN_USER = "login:user:";
}
