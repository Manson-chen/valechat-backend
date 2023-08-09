package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.manager.AiManager;
import com.vale.valechat.mapper.PrivateFileMapper;
import com.vale.valechat.mapper.PrivateMessageMapper;
import com.vale.valechat.mapper.UserMapper;
import com.vale.valechat.mapper.WorkspaceMapper;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.dto.message.PrivateHistoryRequest;
import com.vale.valechat.model.entity.*;
import com.vale.valechat.model.vo.CommonMessageVO;
import com.vale.valechat.model.vo.FileListVO;
import com.vale.valechat.model.vo.SearchMessageVO;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.PrivateMessageService;
import com.vale.valechat.util.CopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage>
    implements PrivateMessageService{

    @Resource
    PrivateMessageMapper privateMessageMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    WorkspaceMapper workspaceMapper;

    @Resource
    PrivateFileMapper privateFileMapper;

    @Resource
    private AiManager aiManager;

    /**
     * Get history message of current user and receiver

     * @param request
     * @return
     */
    // todo 将senderId改为当前用户，参数改为dto
    @Override
    public Page<CommonMessageVO> getHistoryByPrivate(PrivateHistoryRequest privateHistoryRequest, HttpServletRequest request) {

        long senderId = privateHistoryRequest.getSenderId();
        long receiverId = privateHistoryRequest.getReceiverId();
        long workspaceId = privateHistoryRequest.getWorkspaceId();
        long current = privateHistoryRequest.getCurrent();
        long size = privateHistoryRequest.getPageSize();

        if(senderId < 0 || receiverId < 0 || workspaceId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // Verify whether the sender and receiver exists
        // A data manipulation class is installed to set query conditions
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", senderId);
        User sender = userMapper.selectOne(queryWrapper);
        // User does not exist
        if (sender == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "sender does not exist");
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", receiverId);
        User receiver = userMapper.selectOne(queryWrapper);
        // User does not exist
        if (receiver == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "receiver does not exist");
        }

        QueryWrapper<Workspace> workspaceQueryWrapper = new QueryWrapper<>();
        workspaceQueryWrapper.eq("id", workspaceId);
        Workspace workspace = workspaceMapper.selectOne(workspaceQueryWrapper);
        // workspace does not exist
        if (workspace == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "workspace does not exist");
        }

        QueryWrapper<PrivateMessage> privateMessageQueryWrapper = new QueryWrapper<>();
        privateMessageQueryWrapper.and(i -> i.eq("sender_id", senderId).eq("receiver_id", receiverId)
                            .or(j -> j.eq("sender_id", receiverId).eq("receiver_id", senderId)))
                        .eq("workspace_id", workspaceId)
                        .orderByDesc("create_time");
        Page<PrivateMessage> privateMessagePage =  privateMessageMapper.selectPage(new Page<>(current, size), privateMessageQueryWrapper);

        List<CommonMessageVO> messageList = new ArrayList<>();
        Page<CommonMessageVO> commonMessageVOPage = new Page<>();
        BeanUtils.copyProperties(privateMessagePage, commonMessageVOPage);
        for (PrivateMessage privateMessage : privateMessagePage.getRecords()) {
            long messageId = privateMessage.getId();
            CommonMessageVO commonMessageVO = new CommonMessageVO();
            List<FileListVO> fileList = new ArrayList<>();

            QueryWrapper<PrivateFile> fileQueryWrapper = new QueryWrapper<>();
            fileQueryWrapper.eq("message_id", messageId);
            List<PrivateFile> privateFileList = privateFileMapper.selectList(fileQueryWrapper);
            for (PrivateFile privateFile : privateFileList) {
                FileListVO fileListVO = new FileListVO();
                BeanUtils.copyProperties(privateFile, fileListVO);
                fileList.add(fileListVO);
            }
            commonMessageVO.setMsgType(0);
            BeanUtils.copyProperties(privateMessage, commonMessageVO);
            commonMessageVO.setVisibleFileList(fileList);
            messageList.add(commonMessageVO);
        }
        commonMessageVOPage.setRecords(messageList);
        return commonMessageVOPage;
    }

    @Override
    public long saveMessage(CommonMessageRequest commonMessageRequest) {
        PrivateMessage privateMessage = new PrivateMessage();
        BeanUtils.copyProperties(commonMessageRequest, privateMessage);
        int raw = privateMessageMapper.insert(privateMessage);
        if (raw <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Insert message error");
        }
        return privateMessage.getId();
    }

    @Override
    public List<SearchMessageVO> searchChat(long senderId, long receiverId, long workspaceId, String searchWord, long current, long size) {
        // Verify whether the sender and receiver exists
        // A data manipulation class is installed to set query conditions
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", senderId);
        User sender = userMapper.selectOne(queryWrapper);
        // User does not exist
        if (sender == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "sender does not exist");
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", receiverId);
        User receiver = userMapper.selectOne(queryWrapper);
        // User does not exist
        if (receiver == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "receiver does not exist");
        }

        QueryWrapper<Workspace> workspaceQueryWrapper = new QueryWrapper<>();
        workspaceQueryWrapper.eq("id", workspaceId);
        Workspace workspace = workspaceMapper.selectOne(workspaceQueryWrapper);
        // workspace does not exist
        if (workspace == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "workspace does not exist");
        }

        QueryWrapper<PrivateMessage> privateMessageQueryWrapper = new QueryWrapper<>();
        privateMessageQueryWrapper.and(i -> i.eq("sender_id", senderId).eq("receiver_id", receiverId)
                .or(j -> j.eq("sender_id", receiverId).eq("receiver_id", senderId)))
                .eq("workspace_id", workspaceId)
                .like("content",searchWord)
                .orderByDesc("create_time");
        Page<PrivateMessage> messageList = privateMessageMapper.selectPage(new Page<PrivateMessage>(current, size), privateMessageQueryWrapper);
        List<SearchMessageVO> searchResultList = new ArrayList<>();
        searchResultList = CopyUtil.copyList(messageList.getRecords(), SearchMessageVO.class);
        for (SearchMessageVO searchResult : searchResultList) {
            User user = userMapper.selectById(searchResult.getSenderId());
            searchResult.setSenderName(user.getUserName());
        }
        return searchResultList;
    }

    @Override
    public CommonMessageVO chatWithAi(CommonMessageRequest commonMessageRequest) {
        // 保存用户问 ai 的消息
        String content = commonMessageRequest.getContent();
        Long receiverId = commonMessageRequest.getReceiverId();
        Long senderId = commonMessageRequest.getSenderId();
        // 获取 ai 回答
        String aiResponse = aiManager.doChat(content);
        // 封装 ai 回答响应
        CommonMessageRequest aiMessageRequest = new CommonMessageRequest();
        BeanUtils.copyProperties(commonMessageRequest, aiMessageRequest);
        aiMessageRequest.setReceiverId(senderId);
        aiMessageRequest.setContent(aiResponse);
        aiMessageRequest.setSenderId(receiverId);
        CommonMessageVO commonMessageVO = new CommonMessageVO();
        BeanUtils.copyProperties(aiMessageRequest, commonMessageVO);
        long aiMessageId = saveMessage(aiMessageRequest);
        PrivateMessage privateMessage = this.getById(aiMessageId);
        commonMessageVO.setId(aiMessageId);
        commonMessageVO.setCreateTime(privateMessage.getCreateTime());
        return commonMessageVO;
    }
}




