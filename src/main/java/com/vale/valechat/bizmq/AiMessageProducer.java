package com.vale.valechat.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(AiMqConstant.AI_EXCHANGE_NAME, AiMqConstant.AI_ROUTING_KEY, message);
    }
}
