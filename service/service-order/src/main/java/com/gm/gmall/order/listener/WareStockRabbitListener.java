package com.gm.gmall.order.listener;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderInfo;

import com.gm.gmall.model.to.mq.WareStockStatus;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Arrays;

/**
 * @author gym
 * @create 2022/9/18 0018 16:17
 */
@Component
@Slf4j
public class WareStockRabbitListener {

    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    StringRedisTemplate redisTemplate;
    /**
     * 监听减库存
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = RabbitConstant.QUEUE_WARE_ORDER,
                            durable = "true",exclusive = "false",autoDelete = "false"),
                    exchange = @Exchange(name = RabbitConstant.EXCHANGE_WARE_ORDER,
                            durable = "true",autoDelete = "false",type = "direct"),
                    key = RabbitConstant.RK_WARE_ORDER)})
    public void stockDec(Message message, Channel channel) throws IOException {
        WareStockStatus stockStatus = Jsons.toObject(new String(message.getBody()), WareStockStatus.class);
        //查询当前订单,判断状态是否是DEDUCTED或者超库存
        log.info("支付成功，修改当前订单状态");
        Long orderId = stockStatus.getOrderId();
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        ProcessStatus status=null;
        switch (stockStatus.getStatus()){
            case "DEDUCTED": status=ProcessStatus.WAITING_DELEVER;break;
            case "NUM_NOT_ENOUGH": status=ProcessStatus.NOTIFIED_WARE;break;
            default: status=ProcessStatus.PAID;
        }

        //修改订单状态
        orderInfoService.changeStatus(orderId,orderInfo.getUserId(),status, Arrays.asList(ProcessStatus.PAID));
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {

            //失败尝试重试
            Long increment = redisTemplate.opsForValue().increment(RedisConstant.MQ_TEMP + "stock:modify:" + orderId);
            if (increment<=10){
                //没达到重试次数，继续重试
                log.info("重试："+increment+"次");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            }else {
                //达到重试次数，保存数据至持久层，不在重试
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                redisTemplate.delete(RedisConstant.MQ_TEMP+"order:"+orderId);
            }
        }
    }
}
