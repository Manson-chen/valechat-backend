package com.vale.valechat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.message.PrivateHistoryRequest;
import com.vale.valechat.model.dto.message.PrivateMessageRequest;
import com.vale.valechat.model.entity.PrivateMessage;
import com.vale.valechat.model.vo.CommonMessageVO;
import com.vale.valechat.service.PrivateMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/private")
public class PrivateMessageController {

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;


    @Resource
    private PrivateMessageService privateMessageService;

    /**
     * Chat in private and send text messages.
     * @param privateMessageRequest DTO
     */
    // todo change the url in frontend
    @MessageMapping("/message/private")
    public void privateChat(@Payload PrivateMessageRequest privateMessageRequest) {
        if (privateMessageRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        System.out.println(privateMessageRequest);

        long senderId = privateMessageRequest.getSenderId();
        long receiverId = privateMessageRequest.getReceiverId();
//        int type = privateMessageRequest.getType();
        String content = privateMessageRequest.getContent();
        long workspaceId = privateMessageRequest.getWorkspaceId();

        // 1. check parameters
        if (senderId <= 0 || receiverId <= 0 || workspaceId <= 0 || "".equals(content)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. Save the message to database
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(privateMessageRequest, privateMessage);
        privateMessageService.save(privateMessage);

        // 3. Use STOMP to send private message
        /*Using the convertAndSendToUser method, the first parameter is the user id, and the subscription address in js is
         "/user/" + userId + "/message",where "/user" is fixed */
        // /user has been automatically spliced, receiverId subscribes to the push message of this id to realize single-to-single communication
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/message", "Received from " + receiverId + ": " + content);
    }

//    /**
//     * Get current user and receiver's all private chat history
//     * @param privateHistoryRequest privateHistoryRequest
//     * @param request request
//     * @return
//     */
//    @PostMapping("/history")
//    public BaseResponse<Page<CommonMessageVO>> getHistoryByPrivate(@RequestBody PrivateHistoryRequest privateHistoryRequest, HttpServletRequest request){
//        if (privateHistoryRequest == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long senderId = privateHistoryRequest.getSenderId();
//        long receiverId = privateHistoryRequest.getReceiverId();
//        long workspaceId = privateHistoryRequest.getWorkspaceId();
//        if(senderId <= 0 || receiverId <= 0 || workspaceId <= 0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        Page<CommonMessageVO> commonMessageVOPage = privateMessageService.getHistoryByPrivate(privateHistoryRequest, request);
//        return ResultUtils.success(commonMessageVOPage);
//    }

}
