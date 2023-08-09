package com.vale.valechat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vale.valechat.bizmq.AiMessageProducer;
import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.manager.RedisLimiterManager;
import com.vale.valechat.model.dto.message.*;
import com.vale.valechat.model.entity.PrivateMessage;
import com.vale.valechat.model.vo.*;
import com.vale.valechat.service.*;
import com.vale.valechat.service.impl.RedisServiceOld;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/message")
public class CommonMessageController {

    @Resource
    PrivateMessageService privateMessageService;

    @Resource
    ChannelMessageService channelMessageService;

    @Resource
    PrivateFileService privateFileService;

    @Resource
    ChannelFileService channelFileService;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    RedisServiceOld redisServiceOld;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private AiMessageProducer aiMessageProducer;

    @PostMapping("/send")
    public BaseResponse<CommonMessageVO> sendCommonMessage(@Valid CommonMessageRequest commonMessageRequest, BindingResult result, HttpServletRequest request) {
//    @PostMapping("/send/async/mq")
//    public BaseResponse<CommonMessageVO> chatWithAiAsyncMq(@Valid CommonMessageRequest commonMessageRequest, BindingResult result, HttpServletRequest request) {

        if (result.hasErrors()) {
            String errorMsg = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, errorMsg, "");
        }
        CommonMessageVO commonMessageVO = new CommonMessageVO();
        BeanUtils.copyProperties(commonMessageRequest, commonMessageVO);
        long workspaceId = commonMessageRequest.getWorkspaceId();
        long senderId = commonMessageRequest.getSenderId();
        long receiverId = commonMessageRequest.getReceiverId();
        int msgType = commonMessageRequest.getMsgType();
        String content = commonMessageRequest.getContent();
        MultipartFile[] files = commonMessageRequest.getFiles();

        if (StringUtils.isAnyBlank(String.valueOf(workspaceId), String.valueOf(senderId), String.valueOf(receiverId), String.valueOf(msgType))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (content == null && files.length == 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Both content and files are null");
        }

        // redis 中将消息加入未读消息列表
        String redisKey = String.format("valechat:user:unread:%s", receiverId);
        String value;
        if (msgType == 0) {
            value = "user";
        }else {
            value = "channel";
        }
        SetOperations setOperations = redisTemplate.opsForSet();
        if (senderId == msgType) {
            if (msgType != 0) {
                setOperations.add(redisKey, value + senderId);
            }
        } else {
            setOperations.add(redisKey, value + senderId);
        }

        if (receiverId == 0) {
            // 限流操作
            // 限流判断，每个用户一个限流器（这里可以区分用户）
            redisLimiterManager.doRateLimit("chatWithAi_" + senderId);
            // 执行业务操作
            // 先保存问AI的问题
            long messageId = privateMessageService.saveMessage(commonMessageRequest);
            commonMessageVO.setId(messageId);
            Date createTime = privateMessageService.getById(messageId).getCreateTime();
            commonMessageVO.setCreateTime(createTime);

            // 将消息用生产者发到交换机
            aiMessageProducer.sendMessage(String.valueOf(messageId));

            return ResultUtils.success(commonMessageVO);
        }

        if (msgType == 0){
            long messageId = privateMessageService.saveMessage(commonMessageRequest);

            commonMessageVO.setId(messageId);
            Date createTime = privateMessageService.getById(messageId).getCreateTime();
            commonMessageVO.setCreateTime(createTime);
            if (files != null && files.length != 0){
                List<FileListVO> visibleUriList = privateFileService.saveMessageFiles(commonMessageRequest, messageId, files, request);
                commonMessageVO.setVisibleFileList(visibleUriList);
            }
//            List<FileListVO> visibleUriList = privateFileService.saveMessageFiles(commonMessageRequest, messageId, files, request);
//            commonMessageVO.setVisibleUriList(visibleUriList);
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/message", commonMessageVO);
            return ResultUtils.success(commonMessageVO);
        }else if (msgType == 1){
            long messageId = channelMessageService.saveMessage(commonMessageRequest);
            commonMessageVO.setId(messageId);
            Date createTime = channelMessageService.getById(messageId).getCreateTime();
            commonMessageVO.setCreateTime(createTime);
            if (files != null && files.length != 0){
                List<FileListVO> visibleUriList = channelFileService.saveMessageFiles(commonMessageRequest, messageId, files, request);
                commonMessageVO.setVisibleFileList(visibleUriList);
            }
            simpMessagingTemplate.convertAndSend("/topic/channel/" + receiverId, commonMessageVO);
            return ResultUtils.success(commonMessageVO);
        }else{
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "message type is wrong");
        }
    }


    @PostMapping("/send/async")
    public BaseResponse<CommonMessageVO> sendCommonMessageAsync(@Valid CommonMessageRequest commonMessageRequest, BindingResult result, HttpServletRequest request) {

        if (result.hasErrors()) {
            String errorMsg = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, errorMsg, "");
        }
        CommonMessageVO commonMessageVO = new CommonMessageVO();
        BeanUtils.copyProperties(commonMessageRequest, commonMessageVO);
        long workspaceId = commonMessageRequest.getWorkspaceId();
        long senderId = commonMessageRequest.getSenderId();
        long receiverId = commonMessageRequest.getReceiverId();
        int msgType = commonMessageRequest.getMsgType();
        String content = commonMessageRequest.getContent();
        MultipartFile[] files = commonMessageRequest.getFiles();

        if (StringUtils.isAnyBlank(String.valueOf(workspaceId), String.valueOf(senderId), String.valueOf(receiverId), String.valueOf(msgType))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (content == null && files.length == 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Both content and files are null");
        }

        // redis 中将消息加入未读消息列表
        String redisKey = String.format("valechat:user:unread:%s", receiverId);
        String value;
        if (msgType == 0) {
            value = "user";
        }else {
            value = "channel";
        }
        SetOperations setOperations = redisTemplate.opsForSet();
        if (senderId == msgType) {
            if (msgType != 0) {
                setOperations.add(redisKey, value + senderId);
            }
        } else {
            setOperations.add(redisKey, value + senderId);
        }
        // 获取列表是否存在
//        if () {
//            List<String> unreadList = setOperations.pop(redisKey, setOperations.size(redisKey));
//            if (unreadList == null) {
//                unreadSet.add(value + senderId);
//            } else {
//                unreadSet = (Set<String>) unread;
//                unreadSet.add(value + senderId);
//            }
//            setOperations.add(redisKey, value + senderId);
//        }

        if (receiverId == 0) {
            // 限流操作
            // 限流判断，每个用户一个限流器（这里可以区分用户）
            redisLimiterManager.doRateLimit("chatWithAi_" + senderId);
            // 执行业务操作
            long messageId = privateMessageService.saveMessage(commonMessageRequest);
            commonMessageVO.setId(messageId);
            Date createTime = privateMessageService.getById(messageId).getCreateTime();
            commonMessageVO.setCreateTime(createTime);

            CompletableFuture.runAsync(() -> {
                CommonMessageVO aiResponse = privateMessageService.chatWithAi(commonMessageRequest);
                simpMessagingTemplate.convertAndSendToUser(String.valueOf(senderId), "/message", aiResponse);
            }, threadPoolExecutor);
            return ResultUtils.success(commonMessageVO);
        }

        if (msgType == 0){
            long messageId = privateMessageService.saveMessage(commonMessageRequest);

            commonMessageVO.setId(messageId);
            Date createTime = privateMessageService.getById(messageId).getCreateTime();
            commonMessageVO.setCreateTime(createTime);
            if (files != null && files.length != 0){
                List<FileListVO> visibleUriList = privateFileService.saveMessageFiles(commonMessageRequest, messageId, files, request);
                commonMessageVO.setVisibleFileList(visibleUriList);
            }
//            List<FileListVO> visibleUriList = privateFileService.saveMessageFiles(commonMessageRequest, messageId, files, request);
//            commonMessageVO.setVisibleUriList(visibleUriList);
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/message", commonMessageVO);
            return ResultUtils.success(commonMessageVO);
        }else if (msgType == 1){
            long messageId = channelMessageService.saveMessage(commonMessageRequest);
            commonMessageVO.setId(messageId);
            Date createTime = channelMessageService.getById(messageId).getCreateTime();
            commonMessageVO.setCreateTime(createTime);
            if (files != null && files.length != 0){
                List<FileListVO> visibleUriList = channelFileService.saveMessageFiles(commonMessageRequest, messageId, files, request);
                commonMessageVO.setVisibleFileList(visibleUriList);
            }
            simpMessagingTemplate.convertAndSend("/topic/channel/" + receiverId, commonMessageVO);
            return ResultUtils.success(commonMessageVO);
        }else{
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "message type is wrong");
        }
    }

    @PostMapping("/read")
    public BaseResponse<Boolean> markChatAsRead(@RequestBody ReadMessageRequest readMessageRequest) {
        if (readMessageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int msgType = readMessageRequest.getMsgType();
        long senderId = readMessageRequest.getReadChatId();
        long userId = readMessageRequest.getUserId();

        SetOperations setOperations = redisTemplate.opsForSet();
        String redisKey = String.format("valechat:user:unread:%s", userId);
        Long size = setOperations.size(redisKey);
        if (size != null && size > 0){
            if(msgType == 0){
                setOperations.remove(redisKey,"user" + senderId);
            } else{
                setOperations.remove(redisKey,"channel" + senderId);
            }
            if (setOperations.size(redisKey) <= 0) {
                // 要将操作完的set集合返回redis
                setOperations.pop(redisKey);
            }
        }else {
            log.error("mark chat as read error: " + userId);
        }
        return ResultUtils.success(true);
    }
//    public BaseResponse<String> markChatAsRead(@RequestBody ReadMessageRequest readMessageRequest) {
//        if (readMessageRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        int msgType = readMessageRequest.getMsgType();
//        long readChatId = readMessageRequest.getReadChatId();
//        long userId = readMessageRequest.getUserId();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        String dateString = dateFormat.format(date);
//        Map<String, String> map = new HashMap<>();
//        if(msgType == 0){
//            map.put("u" + readChatId,dateString);
//            redisService.setHash("user" + userId, map);
//        } else{
//            map.put("c" + readChatId,dateString);
//            redisService.setHash("user" + userId, map);
//        }
//        return ResultUtils.success(dateString);
//    }

    @PostMapping("/search")
    public BaseResponse<List<SearchMessageVO>> searchChatHistory(@RequestBody ChannelSearchRequest channelSearchRequest) {
        if (channelSearchRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long chatId = channelSearchRequest.getChatId();
        long userId = channelSearchRequest.getCurrentUserId();
        int chatType = channelSearchRequest.getChatType();
        long workspaceId = channelSearchRequest.getWorkspaceId();
        String searchWork = channelSearchRequest.getSearchWord();

        long current = channelSearchRequest.getCurrent();
        long pageSize = channelSearchRequest.getPageSize();

        if (StringUtils.isAnyBlank( String.valueOf(chatId), String.valueOf(userId), String.valueOf(chatType), String.valueOf(workspaceId), searchWork)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<SearchMessageVO> searchMessageVOList;
        if(chatType == 1){
            searchMessageVOList = channelMessageService.searchChannel(chatId,workspaceId,searchWork, current, pageSize);
        }
        else{
            searchMessageVOList = privateMessageService.searchChat(userId,chatId,workspaceId,searchWork, current, pageSize);
        }
        return ResultUtils.success(searchMessageVOList);
    }

    /**
     * Get current user and receiver's all private chat history
     * @param privateHistoryRequest privateHistoryRequest
     * @param request request
     * @return
     */
    @PostMapping("/history/private")
    public BaseResponse<Page<CommonMessageVO>> getHistoryByPrivate(@RequestBody PrivateHistoryRequest privateHistoryRequest, HttpServletRequest request){
        if (privateHistoryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long senderId = privateHistoryRequest.getSenderId();
        long receiverId = privateHistoryRequest.getReceiverId();
        long workspaceId = privateHistoryRequest.getWorkspaceId();
        if(senderId < 0 || receiverId < 0 || workspaceId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<CommonMessageVO> commonMessageVOPage = privateMessageService.getHistoryByPrivate(privateHistoryRequest, request);
        return ResultUtils.success(commonMessageVOPage);
    }


    @PostMapping("/history/channel")
    public BaseResponse<Page<CommonMessageVO>> getHistoryByChannel(@RequestBody ChannelHistoryRequest channelHistory) {
        if (channelHistory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = channelHistory.getChannelId();
        long workspaceId = channelHistory.getWorkspaceId();
        if (StringUtils.isAnyBlank( String.valueOf(channelId), String.valueOf(workspaceId))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<CommonMessageVO> channelHistoryPage = channelMessageService.getChannelHistory(channelHistory);
        return ResultUtils.success(channelHistoryPage);
    }


}
