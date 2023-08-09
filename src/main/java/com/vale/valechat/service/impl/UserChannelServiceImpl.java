package com.vale.valechat.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.mapper.*;
import com.vale.valechat.model.dto.channel.ChannelMemberRequest;
import com.vale.valechat.model.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vale.valechat.model.vo.ChannelUserListVo;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.UserChannelService;
import com.vale.valechat.util.CopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserChannelServiceImpl extends MppServiceImpl<UserChannelMapper, UserChannel>
    implements UserChannelService{
    @Resource
    UserChannelMapper userChannelMapper;
    @Resource
    ChannelMapper channelMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    OrganizationChannelMapper organizationChannelMapper;
    @Resource
    OrganizationMapper organizationMapper;

    /**
     * Get organizations by channelId
     * @param channelId
     * @return
     */
    @Override
    public List<OrganizationVo> GetChannelOrganization(Long channelId) {
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed channel");
        }
        List<OrganizationChannel> organizationChannels = organizationChannelMapper.selectList(new QueryWrapper<OrganizationChannel>()
                .eq("channel_id", channelId));
        List<OrganizationVo> organizationVos = new ArrayList<>();
        if(organizationChannels.size() == 0){
            return organizationVos;
        }
        Set<Long> organizationIds = new HashSet<>();
        for (OrganizationChannel organizationChannel : organizationChannels) {
            organizationIds.add(organizationChannel.getOrganizationId());
        }
        List<Organization> organizations = organizationMapper.selectBatchIds(organizationIds);
        organizationVos = CopyUtil.copyList(organizations, OrganizationVo.class);
        return organizationVos;
    }

    /**
     * Get all organizations' users in the Channel
     * @param channelId
     * @return
     */
    @Override
    public List<UserVO> GetChannelOrganizationUsers(Long channelId) {
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed channel");
        }
        List<OrganizationChannel> organizationChannels = organizationChannelMapper.selectList(new QueryWrapper<OrganizationChannel>()
                .eq("channel_id", channelId));
        List<UserVO> userVOList = new ArrayList<>();
        if(organizationChannels.size() == 0){
            return userVOList;
        }
        Set<Long> organizationIds = new HashSet<>();
        for (OrganizationChannel organizationChannel : organizationChannels) {
            organizationIds.add(organizationChannel.getOrganizationId());
        }
        QueryWrapper<User> userwrapper = new QueryWrapper<User>()
                .in("organization_id", organizationIds);
        userVOList = CopyUtil.copyList(userMapper.selectList(userwrapper), UserVO.class);
        return userVOList;
    }

    /**
     * Get all common users(individual users who are not in organizations) in the Channel
     * @param channelId
     * @return
     */
    @Override
    public List<UserVO> GetChannelCommonMembers(Long channelId) {
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed channel");
        }
        List<UserChannel> userChannels = userChannelMapper.selectList(new QueryWrapper<UserChannel>()
                .eq("channel_id", channelId));
        List<UserVO> userVOList = new ArrayList<>();
        if(userChannels.size() == 0){
            return userVOList;
        }
        Set<Long> userIds = new HashSet<>();
        for (UserChannel UserChannel : userChannels) {
            userIds.add(UserChannel.getUserId());
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        userVOList = CopyUtil.copyList(users, UserVO.class);
        return userVOList;
    }

    /**
     * Get all users and organizations by channelId
     * @param channelMemberRequest
     * @return
     */
    @Override
    public ChannelUserListVo GetChannelAllUserAndOrganization(ChannelMemberRequest channelMemberRequest) {
        long channelId = channelMemberRequest.getChannelId();
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed channel");
        }
        List<UserVO> commonUserVOList = this.GetChannelCommonMembers(channelId);
        List<UserVO> organizationUserVOList = this.GetChannelOrganizationUsers(channelId);
        List<OrganizationVo> organizationVoList = this.GetChannelOrganization(channelId);
        ChannelUserListVo channelUserListVo = new ChannelUserListVo();
        List<UserVO> AllUserVOList = new ArrayList<>();
        AllUserVOList.addAll(commonUserVOList);
        AllUserVOList.addAll(organizationUserVOList);
        channelUserListVo.setUserList(AllUserVOList);
        channelUserListVo.setOrganizationList(organizationVoList);
        return channelUserListVo;
    }

    /**
     * Add users and organizations to the channel
     * @param channelId
     * @param memberList
     * @param organizationList
     * @return
     */
    @Override
    public List<Long> AddMemberToChannel(long channelId, List<Long> organizationList, List<Long> memberList) {
        List<Long> join_memberIdList = new ArrayList<>();
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed channel");
        }

        if(!organizationList.isEmpty()){
            for(long organizationId: organizationList){
                QueryWrapper<Organization> queryOrganizationWrapper = new QueryWrapper<>();
                queryOrganizationWrapper.eq("id", organizationId);
                long result = organizationMapper.selectCount(queryOrganizationWrapper);
                if (result == 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed Organization");
                }
                OrganizationChannel organizationChannel = organizationChannelMapper.findOrganizationChannelByIds(organizationId, channelId);
                if (organizationChannel != null && organizationChannel.getIsDeleted() == 1) {
                    organizationChannelMapper.updateOrganizationChannelDeleted(organizationId,channelId,0);
                } else if (organizationChannel == null) {
                    OrganizationChannel newOrganizationChannel = new OrganizationChannel();
                    newOrganizationChannel.setOrganizationId(organizationId);
                    newOrganizationChannel.setChannelId(channelId);
                    organizationChannelMapper.insert(newOrganizationChannel);
                }
                join_memberIdList.add(organizationId);
            }
        }

        if(!memberList.isEmpty()){
            for(long memberId: memberList){
                QueryWrapper<User> queryUserWrapper = new QueryWrapper<>();
                queryUserWrapper.eq("id", memberId);
                long result = userMapper.selectCount(queryUserWrapper);
                if (result == 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "No existed User");
                }
                UserChannel userChannel = userChannelMapper.findUserChannelByIds(memberId, channelId);
                if (userChannel!= null && userChannel.getIsDeleted() == 1) {
                    userChannelMapper.updateUserChannelDeleted(memberId,channelId,0);
                } else if (userChannel == null) {
                    UserChannel newUserChannel = new UserChannel();
                    newUserChannel.setUserId(memberId);
                    newUserChannel.setChannelId(channelId);
                    userChannelMapper.insert(newUserChannel);
                }
                join_memberIdList.add(memberId);
            }
        }
        return join_memberIdList;
    }

    @Override
    public Long DeleteUserInChannel(Long channelId, Long userId) {
        UpdateWrapper<UserChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .eq("channel_id", channelId)
                .set("is_deleted", 1);
        userChannelMapper.update(null, updateWrapper);
        return userId;
    }

    @Override
    public Long DeleteOrgInChannel(Long channelId, Long organizationId) {
        UpdateWrapper<OrganizationChannel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("organization_id", organizationId)
                .eq("channel_id", channelId)
                .set("is_deleted", 1);
        organizationChannelMapper.update(null, updateWrapper);
        return organizationId;
    }
}




