package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.mapper.*;
import com.vale.valechat.model.dto.message.ChannelHistoryRequest;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.entity.*;
import com.vale.valechat.model.vo.ChannelMessageVo;
import com.vale.valechat.model.vo.CommonMessageVO;
import com.vale.valechat.model.vo.FileListVO;
import com.vale.valechat.model.vo.SearchMessageVO;
import com.vale.valechat.service.ChannelMessageService;
import com.vale.valechat.util.CopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ChannelMessageServiceImpl extends ServiceImpl<ChannelMessageMapper, ChannelMessage>
    implements ChannelMessageService{
    @Resource
    UserMapper userMapper;

    @Resource
    UserChannelMapper userChannelMapper;

    @Resource
    OrganizationChannelMapper organizationChannelMapper;
    @Resource
    ChannelMessageMapper channelMessageMapper;
    @Resource
    OrganizationMapper organizationMapper;

    @Resource
    ChannelFileMapper channelFileMapper;

    @Override
    public Page<CommonMessageVO> getChannelHistory(ChannelHistoryRequest channelHistoryRequest) {
        long channelId = channelHistoryRequest.getChannelId();
        long workspaceId = channelHistoryRequest.getWorkspaceId();
        long current = channelHistoryRequest.getCurrent();
        long size = channelHistoryRequest.getPageSize();

        QueryWrapper<ChannelMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id",channelId)
                .eq("workspace_id",workspaceId).orderByDesc("create_time");
        Page<ChannelMessage> channelMessagePage = this.page(new Page<>(current, size), queryWrapper);

        List<CommonMessageVO> messageList = new ArrayList<>();
        Page<CommonMessageVO> commonMessageVOPage = new Page<>();

        BeanUtils.copyProperties(channelMessagePage, commonMessageVOPage);
        for (ChannelMessage channelMessage : channelMessagePage.getRecords()) {
            long messageId = channelMessage.getId();
            CommonMessageVO commonMessageVO = new CommonMessageVO();
            List<FileListVO> fileList = new ArrayList<>();

            QueryWrapper<ChannelFile> fileQueryWrapper = new QueryWrapper<>();
            fileQueryWrapper.eq("message_id", messageId);
            List<ChannelFile> channelFileList = channelFileMapper.selectList(fileQueryWrapper);
            for (ChannelFile channelFile : channelFileList) {
                FileListVO fileListVO = new FileListVO();
                BeanUtils.copyProperties(channelFile, fileListVO);
                fileList.add(fileListVO);
            }
            commonMessageVO.setMsgType(1);
            commonMessageVO.setReceiverId(channelId);
            BeanUtils.copyProperties(channelMessage, commonMessageVO);
            commonMessageVO.setVisibleFileList(fileList);
            messageList.add(commonMessageVO);
        }
        commonMessageVOPage.setRecords(messageList);
        return commonMessageVOPage;
    }

    @Override
    public ChannelMessageVo LoadChannelOthersLastMessage(long userId, long channelId) {
        QueryWrapper<ChannelMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id",channelId).ne("sender_id",userId).orderByDesc("create_time").last("LIMIT 1");
        ChannelMessage message = channelMessageMapper.selectOne(queryWrapper);
        ChannelMessageVo ChannelMessageVo = CopyUtil.copy(message, ChannelMessageVo.class);
        return ChannelMessageVo;
    }

    @Override
    public List<SearchMessageVO> searchChannel(long channelId, long workspaceId, String searchWord, long current, long size) {
        QueryWrapper<ChannelMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id",channelId)
         //       .eq("workspace_id",workspaceId)
                .like("content",searchWord)
                .orderByDesc("create_time");
        Page<ChannelMessage> messageList = channelMessageMapper.selectPage(new Page<ChannelMessage>(current, size), queryWrapper);
        List<SearchMessageVO> searchResultList = new ArrayList<>();
        searchResultList = CopyUtil.copyList(messageList.getRecords(), SearchMessageVO.class);
        for (SearchMessageVO searchResult : searchResultList) {
            User user = userMapper.selectById(searchResult.getSenderId());
            searchResult.setSenderName(user.getUserName());
            if(user.getUserRole() == 1){
                Organization organization = organizationMapper.selectById(user.getOrganizationId());
                searchResult.setSenderOrganizationId(organization.getId());
                searchResult.setSenderOrganizationName(organization.getOrganizationName());
            }
        }
        return searchResultList;
    }

    @Override
    public long saveMessage(CommonMessageRequest commonMessageRequest) {
        ChannelMessage channelMessage = new ChannelMessage();
        BeanUtils.copyProperties(commonMessageRequest, channelMessage);
        channelMessage.setChannelId(commonMessageRequest.getReceiverId());
        int raw = channelMessageMapper.insert(channelMessage);
        if (raw <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Insert message error");
        }
        return channelMessage.getId();
    }

//    @Override
//    public List<ChannelMessageVo> LoadUOChannelHistory(long userId, long organizationId, long workspaceId) {
//        QueryWrapper<OrganizationChannel> orgchannelWrapper = new QueryWrapper<>();
//        orgchannelWrapper.eq("organization_id", organizationId).
//                orderByDesc("create_time");
//        QueryWrapper<ChannelMessage> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("channel_type", 1)
//                .eq("workspace_id",workspaceId).
//                orderByDesc("create_time");
//        List<ChannelMessage> messageList = this.list(queryWrapper);
//        List<ChannelMessageVo> channelmessageList = CopyUtil.copyList(messageList, ChannelMessageVo.class);
//        for(ChannelMessageVo messageVo : channelmessageList){
//            QueryWrapper<User> wrapper = new QueryWrapper<>();
//            wrapper.eq("id",messageVo.getSenderId());
//            messageVo.setSenderName( userMapper.selectOne(wrapper).getUserName());
//        }
//        return channelmessageList;
//    }
}