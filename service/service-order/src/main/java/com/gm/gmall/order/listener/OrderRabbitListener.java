package com.gm.gmall.order.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.payment.PaymentInfo;
import com.gm.gmall.model.to.mq.WareStockMsg;
import com.gm.gmall.model.to.mq.WareStockSkuInfo;
import com.gm.gmall.model.vo.order.OrderMsg;
import com.gm.gmall.order.service.OrderDetailService;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.order.service.OrderService;
import com.gm.gmall.order.service.PaymentInfoService;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gym
 * @create 2022/9/14 0014 18:51
 */
@Component
@Slf4j
public class OrderRabbitListener {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderService orderService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 监听死信队列的消息
     * 实现延迟关单功能
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(queues = RabbitConstant.ORDER_DEAD_QUEUE)
    public void deadQueueListener(Message message, Channel channel) throws Exception{
        String s = new String(message.getBody());
        OrderMsg orderMsg = Jsons.toObject(s, OrderMsg.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("监听到订单消息，开始关闭订单");
            orderService.closeOrder(orderMsg);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            //失败尝试重试
            Long increment = redisTemplate.opsForValue().increment(RedisConstant.MQ_TEMP + "order:" + orderMsg.getOrderId());
            if (increment<=10){
                //没达到重试次数，继续重试
                log.info("重试："+increment+"次");
                channel.basicNack(deliveryTag,false,true);
            }else {
                //达到重试次数，保存数据至持久层，不在重试
                channel.basicNack(deliveryTag,false,false);
                redisTemplate.delete(RedisConstant.MQ_TEMP+"order:"+orderMsg.getOrderId());
            }
        }
    }

    /**
     * 监听订单支付成功队列
     * 进行更改订单状态
     */
    @RabbitListener(queues = RabbitConstant.ORDER_PAYED_QUEUE)
    public void payedQueueListener(Message message, Channel channel) throws IOException {
        log.info("监听到订单支付成功");
        String s = new String(message.getBody());
        Map<String,String> map = Jsons.toObject(s, Map.class);
        //保存支付消息
        PaymentInfo info = paymentInfoService.savePayment(map);
        //修改订单状态
        orderInfoService.changeStatus(info.getOrderId(),info.getUserId(), ProcessStatus.PAID,Arrays.asList(ProcessStatus.UNPAID,ProcessStatus.CLOSED));
        //修改库存
        WareStockMsg msg=new WareStockMsg();
        msg.setOrderId(info.getOrderId());
       OrderInfo orderInfo= orderInfoService.getByUserIdAndOrderId(info.getUserId(),info.getOrderId());
        msg.setConsignee(orderInfo.getConsignee());
        msg.setConsigneeTel(orderInfo.getConsigneeTel());
        msg.setOrderComment(orderInfo.getOrderComment());
        msg.setOrderBody(orderInfo.getTradeBody());
        msg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        msg.setPaymentWay("2");
        //查询订单详情
        List<OrderDetail> orderDetails = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orderInfo.getId())
                .eq(OrderDetail::getUserId, info.getUserId()));
        List<WareStockSkuInfo> wareStockSkuInfos = orderDetails.stream().map(orderDetail -> {
            WareStockSkuInfo stockSkuInfo = new WareStockSkuInfo();
            stockSkuInfo.setSkuId(orderDetail.getSkuId());
            stockSkuInfo.setSkuNum(orderDetail.getSkuNum());
            stockSkuInfo.setSkuName(orderDetail.getSkuName());
            return stockSkuInfo;
        }).collect(Collectors.toList());
        msg.setDetails(wareStockSkuInfos);
        //发送消息修改订单存库
        rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_WARE_EVENT,RabbitConstant.RK_WARE_DEDUCE,Jsons.toJson(msg));
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            //失败尝试重试
            Long increment = redisTemplate.opsForValue().increment(RedisConstant.MQ_TEMP + "stock:" + msg.getOrderId());
            if (increment<=10){
                //没达到重试次数，继续重试
                log.info("重试："+increment+"次");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            }else {
                //达到重试次数，保存数据至持久层，不在重试
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                redisTemplate.delete(RedisConstant.MQ_TEMP+"order:"+msg.getOrderId());
            }
        }

    }
}
