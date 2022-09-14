package com.gm.gmall.order.listener;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.vo.order.OrderMsg;
import com.gm.gmall.order.service.OrderService;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
}
