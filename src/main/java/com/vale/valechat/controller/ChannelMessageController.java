package com.vale.valechat.controller;

import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.message.ChannelMessageRequest;
import com.vale.valechat.model.entity.ChannelMessage;
import com.vale.valechat.service.ChannelMessageService;
import com.vale.valechat.service.impl.RedisServiceOld;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ChannelMessageController {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Resource
    private ChannelMessageService channelMessageService;

    @Resource
    private RedisServiceOld redisServiceOld;

    @MessageMapping("/channel")
    public void channelChat(@Payload ChannelMessageRequest channelMessageRequest) {
        if (channelMessageRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        long senderId = channelMessageRequest.getSenderId();
        long channelId = channelMessageRequest.getChannelId();
//        int type = channelMessageRequest.getType();
        String content = channelMessageRequest.getContent();
        long workspaceId = channelMessageRequest.getWorkspaceId();

        // 1. check parameters
        if (senderId <= 0 || channelId <= 0 || workspaceId <= 0 || "".equals(content)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. Save the message to database
        ChannelMessage channelMessage = new ChannelMessage();
        BeanUtils.copyProperties(channelMessageRequest, channelMessage);
        channelMessageService.save(channelMessage);

        // 3. Use STOMP to send channel message
        simpMessagingTemplate.convertAndSend("/topic/channel/" + channelId, channelMessage);
    }

//    @MessageMapping("/message/read")
//    public void markMessageAsRead(@Payload ReadMessageRequest readMessageRequest) {
//        if (readMessageRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long channelId = readMessageRequest.getChannelId();
//        long userId = readMessageRequest.getUserId();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        String dateString = dateFormat.format(date);
//        Map<String, String> map = new HashMap<>();
//        map.put(String.valueOf(channelId),dateString);
//        redisService.setHash("user" + userId, map);
//    }

//    @PostMapping("/unreadchannel")
//    public BaseResponse<List<Long>> GetUserUnreadChannel(@Payload ChannelGetRequest channelGetRequest) {
//        if (channelGetRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long workspaceIdId = channelGetRequest.getWorkspaceId();
//        long userId = 1;
//        System.out.println("userId:"+ userId);
//        List<Long> unreadChannel = new ArrayList<>();
//        List<Long> channelIdList = new ArrayList<>();
//        channelIdList.add((long)1);
//        channelIdList.add((long)2);
//    //    channelIdList.add((long)3);
//        for(Long channelId: channelIdList){
//            ChannelMessageVo channelMessageVo = channelMessageService.LoadChannelOthersLastMessage(userId, channelId);
//            if(channelMessageVo != null) {
//                Date channelLastMessageTime = channelMessageVo.getCreateTime();
//                Date userLastReadTime = redisService.getLastReadTime("user" + userId, "c" + channelId);
//                if ((userLastReadTime == null) || userLastReadTime.compareTo(channelLastMessageTime) < 0) {
//                    unreadChannel.add(channelId);
//                }
//            }
//        }
//        return ResultUtils.success(unreadChannel);
//    }

//    @PostMapping("/history/channel")
//    public BaseResponse<Page<CommonMessageVO>> getHistoryByChannel(@RequestBody ChannelHistoryRequest channelHistory) {
//        if (channelHistory == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long channelId = channelHistory.getChannelId();
//        long workspaceId = channelHistory.getWorkspaceId();
//        if (StringUtils.isAnyBlank( String.valueOf(channelId), String.valueOf(workspaceId))) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Page<CommonMessageVO> channelHistoryPage = channelMessageService.getChannelHistory(channelHistory);
//        return ResultUtils.success(channelHistoryPage);
//    }

//    @PostMapping("/search/channel")
//    public BaseResponse<List<ChannelMessageVo>> SearchHistoryByChannel(@RequestBody ChannelSearchRequest channelSearchRequest) {
//        if (channelSearchRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long channelId = channelSearchRequest.getChatId();
//        long workspaceId = channelSearchRequest.getWorkspaceId();
//        String searchWork = channelSearchRequest.getSearchWord();
//        if (StringUtils.isAnyBlank( String.valueOf(channelId), String.valueOf(workspaceId), searchWork)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        return ResultUtils.success( channelMessageService.SearchChannel(channelId,workspaceId,searchWork));
//    }
}
