package com.gm.gmall.seckill.listener;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.to.mq.SeckillMqMsg;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import com.gm.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;


/**
 * @author gym
 * @create 2022/9/20 0020 8:35
 */
@Component
@Slf4j
public class SeckillListener {
    @Autowired
    SeckillGoodsService goodsService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(name = RabbitConstant.SECKILL_QUEUE,
                                            durable = "true",exclusive = "false",autoDelete = "false"),
                          exchange = @Exchange(name = RabbitConstant.EXCHANGE_SECKILL),
                          key = RabbitConstant.SECKILL_DEC_RK)
    })
    public void seckill(Message message, Channel channel) throws IOException {
        log.info("监听到秒杀订单消息");
        SeckillMqMsg seckillMqMsg = Jsons.toObject(new String(message.getBody()), SeckillMqMsg.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info(seckillMqMsg.toString());
        try {
            //更改库存
            goodsService.dscStock(seckillMqMsg.getSkuId());
            //更改redis的订单数据
            String s = redisTemplate.opsForValue().get(RedisConstant.SECKILL_ORDER + seckillMqMsg.getSkuIdStr());
            OrderInfo orderInfo = Jsons.toObject(s, OrderInfo.class);
            orderInfo.getOrderDetailList().get(0).setSkuNum(orderInfo.getOrderDetailList().get(0).getSkuNum()-1);
            orderInfo.setOperateTime(new Date());
            redisTemplate.opsForValue().set(RedisConstant.SECKILL_ORDER+seckillMqMsg.getSkuIdStr(),Jsons.toJson(orderInfo));
            channel.basicAck(deliveryTag,false);
        } catch (DataIntegrityViolationException e) {
            //sql错误,将redis中的
            redisTemplate.opsForValue().set(RedisConstant.SECKILL_ORDER+seckillMqMsg.getSkuIdStr(),"X");
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            //失败尝试重试
            Long increment = redisTemplate.opsForValue().increment(RedisConstant.MQ_TEMP + "seckill:" + seckillMqMsg.getSkuIdStr());
            if (increment<=10){
                //没达到重试次数，继续重试
                log.info("重试："+increment+"次");
                channel.basicNack(deliveryTag,false,true);
            }else {
                //达到重试次数，保存数据至持久层，不在重试
                channel.basicNack(deliveryTag,false,false);
                redisTemplate.delete(RedisConstant.MQ_TEMP + "seckill:" + seckillMqMsg.getSkuIdStr());
            }
        }

    }
}
