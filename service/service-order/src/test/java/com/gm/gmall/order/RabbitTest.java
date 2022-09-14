package com.gm.gmall.order;

import com.gm.gmall.rabbit.consatant.RabbitConstant;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gym
 * @create 2022/9/14 0014 18:39
 */
@SpringBootTest
public class RabbitTest {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    public void test(){
        rabbitTemplate.convertAndSend(RabbitConstant.ORDER_CHANGE,RabbitConstant.ORDER_DELAY_RK,"11");

    }
}
