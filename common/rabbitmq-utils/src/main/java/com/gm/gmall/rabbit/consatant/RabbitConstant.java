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
//    public static final int TTL = 1000*30;
    public static final int TTL = 1000 * 60 * 15;
    public static final String ORDER_PAYED_QUEUE = "order-payed-queue";
    public static final String ORDER_PAYED_RK = "order-payed";

    //库存交换机
    public static final String EXCHANGE_WARE_EVENT = "exchange.direct.ware.stock";
    //减库存路由键
    public static final String RK_WARE_DEDUCE = "ware.stock";
    //库存扣减结果队列
    public static final String QUEUE_WARE_ORDER = "queue.ware.order";
    //库存扣减交换机
    public static final String EXCHANGE_WARE_ORDER = "exchange.direct.ware.order";
    //库存扣减路由键
    public static final String RK_WARE_ORDER = "ware.order";
}
