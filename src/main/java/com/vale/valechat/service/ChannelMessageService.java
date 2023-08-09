package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.dto.message.ChannelHistoryRequest;
import com.vale.valechat.model.dto.message.CommonMessageRequest;
import com.vale.valechat.model.entity.ChannelMessage;
import com.vale.valechat.model.vo.ChannelMessageVo;
import com.vale.valechat.model.vo.CommonMessageVO;
import com.vale.valechat.model.vo.SearchMessageVO;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【channel_message】的数据库操作Service
* @createDate 2023-04-05 19:38:14
*/
public interface ChannelMessageService extends IService<ChannelMessage> {
    Page<CommonMessageVO> getChannelHistory(ChannelHistoryRequest channelHistory);

    ChannelMessageVo LoadChannelOthersLastMessage(long userId, long channelId);

    List<SearchMessageVO> searchChannel(long channelId, long workspaceId, String searchWord, long current, long pageSize);

    long saveMessage(CommonMessageRequest commonMessageRequest);
}
