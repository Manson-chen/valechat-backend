package com.vale.valechat.controller;

import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.channel.ChannelMemberRequest;
import com.vale.valechat.model.dto.channel.MemberAddRequest;
import com.vale.valechat.model.dto.channel.OrgDeleteRequest;
import com.vale.valechat.model.dto.channel.UserDeleteRequest;
import com.vale.valechat.model.vo.ChannelUserListVo;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.OrganizationChannelService;
import com.vale.valechat.service.UserChannelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/userchannel")
public class UserChannelController {
    @Resource
    UserChannelService userChannelService;

    /**
     * Get the channel's all users(including the common user and organization user) and organizations.
     * @param channelMemberRequest
     * @return
     */
    @PostMapping("/usersandorganizations")
    public BaseResponse<ChannelUserListVo> GetChannelUsersAndOrganization (@RequestBody ChannelMemberRequest channelMemberRequest) {
        if (channelMemberRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = channelMemberRequest.getChannelId();
        if ((StringUtils.isAnyBlank(String.valueOf(channelId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ChannelUserListVo channelUserListVo = userChannelService.GetChannelAllUserAndOrganization(channelMemberRequest);
        return ResultUtils.success(channelUserListVo);
    }

    /**
     * Get the Channel common user
     * @param channelMemberRequest
     * @return
     */
    @PostMapping("/commonuser")
    public BaseResponse<List<UserVO>> GetChannelCommonUser (@RequestBody ChannelMemberRequest channelMemberRequest) {
        if (channelMemberRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = channelMemberRequest.getChannelId();
        if ((StringUtils.isAnyBlank(String.valueOf(channelId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> channelMemberList = userChannelService.GetChannelCommonMembers(channelId);
        return ResultUtils.success(channelMemberList);
    }

    /**
     * Get the channel's organization user
     * @param channelMemberRequest
     * @return
     */
    @PostMapping("/organizationuser")
    public BaseResponse<List<UserVO>> GetChannelOrganizationUser (@RequestBody ChannelMemberRequest channelMemberRequest) {
        if (channelMemberRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = channelMemberRequest.getChannelId();
        if ((StringUtils.isAnyBlank(String.valueOf(channelId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> channelMemberList = userChannelService.GetChannelOrganizationUsers(channelId);
        return ResultUtils.success(channelMemberList);
    }

    /**
     * Get the channel's Organization
     * @param channelMemberRequest
     * @return
     */
    @PostMapping("/organization")
    public BaseResponse<List<OrganizationVo>> GetChannelOrganization (@RequestBody ChannelMemberRequest channelMemberRequest) {
        if (channelMemberRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = channelMemberRequest.getChannelId();
        if ((StringUtils.isAnyBlank(String.valueOf(channelId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<OrganizationVo> organizationList = userChannelService.GetChannelOrganization(channelId);
        return ResultUtils.success(organizationList);
    }

    /**
     * Add the organizations and users to the channel
     * @param memberAddRequest
     * @return
     */
    @PostMapping("/addmember")
    public BaseResponse<List<Long>> AddMemberToChannel(@RequestBody MemberAddRequest memberAddRequest){
        if (memberAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = memberAddRequest.getChannelId();
        List<Long> organizationList = memberAddRequest.getOrganizationIdList();
        List<Long> memberIdList = memberAddRequest.getMemberIdList();
        if ((StringUtils.isAnyBlank(String.valueOf(channelId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Long> joined_memberIdList = userChannelService.AddMemberToChannel(channelId, organizationList, memberIdList);
        return ResultUtils.success(joined_memberIdList);
    }

    @PostMapping("/deleteorg")
    public BaseResponse<Long> DeleteOrgInChannel(@RequestBody OrgDeleteRequest orgDeleteRequest){
        if (orgDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = orgDeleteRequest.getChannelId();
        long organizationId = orgDeleteRequest.getOrganizationId();
        if (StringUtils.isAnyBlank(String.valueOf(channelId), String.valueOf(organizationId))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long deleteResult = userChannelService.DeleteOrgInChannel(channelId, organizationId);
        return ResultUtils.success(deleteResult);
    }

    @PostMapping("/deletemember")
    public BaseResponse<Long> DeleteUserInChannel (@RequestBody UserDeleteRequest userDeleteRequest){
        if (userDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId = userDeleteRequest.getChannelId();
        long userId = userDeleteRequest.getUserId();
        if (StringUtils.isAnyBlank( String.valueOf(channelId), String.valueOf(userId))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long deleteResult = userChannelService.DeleteUserInChannel(channelId, userId);
        return ResultUtils.success(deleteResult);
    }
}
