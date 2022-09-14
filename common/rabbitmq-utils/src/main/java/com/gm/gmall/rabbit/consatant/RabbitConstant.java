package com.gm.gmall.rabbit.consatant;

/**
 * @author gym
 * @create 2022/9/14 0014 16:56
 */
public class RabbitConstant {

    public static final String ORDER_CHANGE = "order-exchange";
    public static final String ORDER_DELAY_QUEUE = "order-delay-queue";
    public static final String ORDER_DELAY_RK = "order-dead";
    public static final String ORDER_CREATED_RK = "order-created";
    public static final String ORDER_DEAD_QUEUE = "order-dead-queue";
    public static final int TTL = 1000*30;
//    public static final int TTL = 1000 * 60 * 15;
}
