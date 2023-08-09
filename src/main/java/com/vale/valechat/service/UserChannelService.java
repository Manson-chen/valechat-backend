package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jeffreyning.mybatisplus.service.IMppService;

import com.vale.valechat.model.dto.channel.ChannelMemberRequest;
import com.vale.valechat.model.entity.UserChannel;
import com.vale.valechat.model.entity.Channel;
import com.vale.valechat.model.vo.ChannelUserListVo;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【user_channel】的数据库操作Service
* @createDate 2023-04-05 19:35:08
*/
public interface UserChannelService extends IMppService<UserChannel> {
   // List<Channel> GetUserAllChannel(Long userId, Long workspaceId);

    ChannelUserListVo GetChannelAllUserAndOrganization(ChannelMemberRequest channelMemberRequest);
    List<OrganizationVo> GetChannelOrganization(Long channelId);
    List<UserVO> GetChannelCommonMembers(Long channelId);
    List<UserVO> GetChannelOrganizationUsers(Long channelId);
   // List<UserVO> GetChannelMembers(Long channelId);
    List<Long> AddMemberToChannel(long channelId, List<Long> organizationList, List<Long> memberList);
    Long DeleteUserInChannel(Long channelId, Long userId);
    Long DeleteOrgInChannel(Long channelId, Long organizationId);
}
