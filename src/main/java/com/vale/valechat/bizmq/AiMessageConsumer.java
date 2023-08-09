package com.vale.valechat.bizmq;

import com.rabbitmq.client.Channel;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.manager.AiManager;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.entity.PrivateMessage;
import com.vale.valechat.model.vo.CommonMessageVO;
import com.vale.valechat.service.PrivateMessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class AiMessageConsumer {

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private AiManager aiManager;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private RedisTemplate redisTemplate;


    @SneakyThrows
    @RabbitListener(queues = {AiMqConstant.AI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveAndAddressMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("消费者接受到消息");
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            log.error("消费者：消息为空");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息为空");
        }
        long messageId = Long.parseLong(message);
        PrivateMessage aiMessage = privateMessageService.getById(messageId);
        if (aiMessage == null) {
            channel.basicNack(deliveryTag, false, false);
            log.error("消费者：数据库中不存在该消息");
            throw new BusinessException(ErrorCode.NULL_ERROR, "数据库中不存在该消息");
        }
        Long senderId = aiMessage.getSenderId();
        Long receiverId = aiMessage.getReceiverId();
        Long workspaceId = aiMessage.getWorkspaceId();
        String content = aiMessage.getContent();

        String aiResponse = aiManager.doChat(content);
        if (StringUtils.isBlank(aiResponse)) {
            channel.basicNack(deliveryTag, false, false);
            log.error("消费者：AI 生成回答错误");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 生成回答错误");
        }
        // 保存 AI 响应
        CommonMessageRequest aiMessageRequest = new CommonMessageRequest();
        aiMessageRequest.setSenderId(receiverId);
        aiMessageRequest.setReceiverId(senderId);
        aiMessageRequest.setContent(aiResponse);
        aiMessageRequest.setWorkspaceId(workspaceId);
        aiMessageRequest.setMsgType(0);
        long saveMessageId = privateMessageService.saveMessage(aiMessageRequest);
        if (saveMessageId < 0 || StringUtils.isBlank(String.valueOf(saveMessageId))) {
            channel.basicNack(deliveryTag, false, false);
            log.error("消费者：保存AI回答消息出错");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存AI回答消息出错");
        }

        // 封装 AI 回答
        CommonMessageVO commonMessageVO = new CommonMessageVO();
        BeanUtils.copyProperties(aiMessageRequest, commonMessageVO);
        PrivateMessage privateMessage = privateMessageService.getById(saveMessageId);
        if (privateMessage == null) {
            channel.basicNack(deliveryTag, false, false);
            log.error("消费者：从数据库获取消息出错");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "从数据库获取消息出错");
        }
        commonMessageVO.setId(saveMessageId);
        commonMessageVO.setCreateTime(privateMessage.getCreateTime());

        // 确认消息
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("确认消息出错" + e);
        }

        // redis 中将消息加入未读消息列表
        // 最好将以下操作封装成通用方法
        String redisKey = String.format("valechat:user:unread:%s", senderId);
        String value = "user";
        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.add(redisKey, value + receiverId);

        // 将AI回答返回给用户
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(senderId), "/message", commonMessageVO);
    }

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL") //手动确认模式
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        channel.basicAck(deliveryTag, false);
    }
}
