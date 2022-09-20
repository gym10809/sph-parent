package com.gm.gmall.seckill.listener;

import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.to.mq.SeckillMqMsg;
import com.gm.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * @author gym
 * @create 2022/9/20 0020 8:35
 */
@Component
@Slf4j
public class SeckillListener {
    @Autowired
    SeckillGoodsService goodsService;
    @RabbitListener
    public void seckill(Message message, Channel channel){
        log.info("监听到秒杀订单消息");
        SeckillMqMsg seckillMqMsg = Jsons.toObject(new String(message.getBody()), SeckillMqMsg.class);
        log.info(seckillMqMsg.toString());
        //更改库存
        goodsService.dscStock(seckillMqMsg.getSkuId());
    }
}
