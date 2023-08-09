package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.vale.valechat.mapper.OrganizationMapper;
import com.vale.valechat.mapper.OrganizationWorkspaceMapper;
import com.vale.valechat.mapper.UserMapper;
import com.vale.valechat.mapper.UserWorkspaceMapper;
import com.vale.valechat.model.entity.*;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.UserWorkspaceService;
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
public class UserWorkspaceServiceImpl extends MppServiceImpl<UserWorkspaceMapper, UserWorkspace>
    implements UserWorkspaceService{
    @Resource
    UserMapper userMapper;
    @Resource
    OrganizationMapper organizationMapper;
    @Resource
    OrganizationWorkspaceMapper organizationWorkspaceMapper;
    @Override
    public List<UserVO> GetWorkSpaceMembers(Long workspaceId) {
        QueryWrapper<UserWorkspace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("workspace_id", workspaceId);
        List<UserWorkspace> userList = this.list(queryWrapper);
        List<Long> userIdList = new ArrayList<>();
        List<User> ChannelMemberList = new ArrayList<>();
        userList .forEach(user->{
            userIdList.add(user.getUserId());
        });
        for(long userId: userIdList){
            QueryWrapper<User> queryUserWrapper = new QueryWrapper<>();
            queryUserWrapper.eq("id", userId);
            ChannelMemberList.add(userMapper.selectOne(queryUserWrapper));
        }
        List<UserVO> userVOList = CopyUtil.copyList(ChannelMemberList, UserVO.class);
        return userVOList;
    }

    @Override
    public List<OrganizationVo> GetWorkSpaceOrganizations(Long workspaceId) {
        QueryWrapper<OrganizationWorkspace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("workspace_id", workspaceId);
        List<OrganizationWorkspace> organizationWorkspaceList = organizationWorkspaceMapper.selectList(queryWrapper);
        List<Long> organizationIdList = new ArrayList<>();
        List<Organization> WorkspaceOrganizationList = new ArrayList<>();
        organizationWorkspaceList .forEach(organizationWorkspace->{
            organizationIdList.add(organizationWorkspace.getOrganizationId());
        });
        for(long organizationId: organizationIdList){
            QueryWrapper<Organization> queryUserWrapper = new QueryWrapper<>();
            queryUserWrapper.eq("id", organizationId);
            WorkspaceOrganizationList.add(organizationMapper.selectOne(queryUserWrapper));
        }
        List<OrganizationVo> OrganizationVoList = CopyUtil.copyList(WorkspaceOrganizationList, OrganizationVo.class);
        return OrganizationVoList;
    }
}




