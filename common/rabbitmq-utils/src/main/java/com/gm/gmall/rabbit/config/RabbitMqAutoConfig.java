package com.gm.gmall.rabbit.config;


import com.gm.gmall.rabbit.consatant.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author gym
 * @create 2022/9/14 0014 15:21
 */
@Slf4j
@Configuration
public class RabbitMqAutoConfig {

    /**
     * 设置消息可靠性传递
     * @param configurer
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate,connectionFactory);
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                //如果消息传递至队列失败，则会返回消息
                log.error("消息传递至队列失败"+replyText);
            }
        });
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack){
                    //消息传递至交换机失败
                    log.error("消息传递至交换机失败"+cause);

                }
            }
        });
        //开启重试机制默认三次
        rabbitTemplate.setRetryTemplate(new RetryTemplate());
        return rabbitTemplate;
    }


}
