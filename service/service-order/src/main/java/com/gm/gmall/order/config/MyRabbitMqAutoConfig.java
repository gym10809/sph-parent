package com.gm.gmall.order.config;

import com.gm.gmall.rabbit.consatant.RabbitConstant;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gym
 * @create 2022/9/14 0014 18:31
 */
@Configuration
public class MyRabbitMqAutoConfig {
    /**
     * 创建交换机
     * @return
     */
    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(RabbitConstant.ORDER_CHANGE).durable(true).build();
    }

    /**
     * 创建延迟队列
     * @return
     */
    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable(RabbitConstant.ORDER_DELAY_QUEUE)
                .deadLetterExchange(RabbitConstant.ORDER_CHANGE)
                .deadLetterRoutingKey(RabbitConstant.ORDER_DELAY_RK)
                .ttl(RabbitConstant.TTL)
                .build();
    }
    /**
     * 绑定延迟队列和交换机
     */
    @Bean
    public Binding bindExchangeDelayQueue(@Qualifier("exchange") Exchange exchange, @Qualifier("delayQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConstant.ORDER_CREATED_RK).noargs();
    }
    /**
     * 创建死信队列
     */
    @Bean
    public Queue deadQueue(){
        return QueueBuilder.durable(RabbitConstant.ORDER_DEAD_QUEUE).build();
    }
    /**
     * 绑定死信队列和交换机
     */
    @Bean
    public Binding bindDeadQueueExchange(@Qualifier("exchange") Exchange exchange, @Qualifier("deadQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConstant.ORDER_DELAY_RK).noargs();
    }
}
