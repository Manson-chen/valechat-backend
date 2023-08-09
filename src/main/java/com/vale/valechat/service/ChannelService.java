package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.entity.Channel;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【channel】的数据库操作Service
* @createDate 2023-04-05 19:34:49
*/
public interface ChannelService extends IService<Channel> {
    Channel CreateNewChannel(Long masterId, Long workspaceId, String ChannelName);

    long UpdateChannel(long masterId, long workspaceId, String ChannelName);

    long createNewUserOrgChannel(Long userId, Long workspaceId);

    long GetChannelMasterId(Long channelId);
}
