package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.mapper.*;
import com.vale.valechat.model.entity.Channel;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.entity.OrganizationChannel;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.entity.UserChannel;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel>
    implements ChannelService{
    @Resource
    ChannelMapper channelMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    UserChannelMapper userChannelMapper;
    @Resource
    OrganizationChannelMapper organizationChannelMapper;
    @Resource
    OrganizationMapper organizationMapper;

    @Override
    public Channel CreateNewChannel(Long masterId, Long workspaceId, String GroupName) {
        Channel channel = new Channel();
        channel.setMasterId(masterId);
        channel.setWorkspaceId(workspaceId);
        channel.setChannelName(GroupName);
        QueryWrapper<User> queryUserWrapper = new QueryWrapper<>();
        queryUserWrapper.eq("id", masterId);
        long result = userMapper.selectCount(queryUserWrapper);
        if (result == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed User");
        }
        boolean saveResult = this.save(channel);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Insert database error");
        }
        return channel;
    }

    @Override
    public long UpdateChannel(long Id, long workspaceId, String ChannelName) {
        Channel channel = new Channel();
        channel.setId(Id);
        channel.setChannelName(ChannelName);
        this.updateById(channel);
        return Id;
    }

    @Override
    public long createNewUserOrgChannel(Long userId, Long workspaceId) {
        QueryWrapper<User> queryUserWrapper = new QueryWrapper<>();
        queryUserWrapper.eq("id", userId);
        long result = userMapper.selectCount(queryUserWrapper);
        if (result == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed User");
        }

        List<OrganizationVo> OrganizationVoList = organizationMapper.getOrganizationInWorkspace(new QueryWrapper<>(), workspaceId);
        for(OrganizationVo organizationVo: OrganizationVoList){
            Channel channel = new Channel();
            channel.setMasterId(userId);
            channel.setWorkspaceId(workspaceId);
            channel.setChannelType(1);
            channel.setChannelName(organizationVo.getOrganizationName());
            boolean saveResult = this.save(channel);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Insert database error");
            }
            UserChannel userChannel = new UserChannel();
            userChannel.setChannelId(channel.getId());
            userChannel.setUserId(userId);
            userChannelMapper.insert(userChannel);

            OrganizationChannel organizationChannel = new OrganizationChannel();
            organizationChannel.setChannelId(channel.getId());
            organizationChannel.setOrganizationId(organizationVo.getId());
            organizationChannelMapper.insert(organizationChannel);
        }
        return userId;
    }

    @Override
    public long GetChannelMasterId(Long channelId) {
        Channel channel = channelMapper.selectById(channelId);
        return channel.getMasterId();
    }
}




