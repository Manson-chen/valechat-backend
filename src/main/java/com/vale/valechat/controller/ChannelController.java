package com.vale.valechat.controller;

import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.channel.*;
import com.vale.valechat.model.entity.Channel;
import com.vale.valechat.service.ChannelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/channel")
public class ChannelController {
    @Resource
    private ChannelService channelService;
    @PostMapping("/newChannel")
    public BaseResponse<Channel> CreateNewChannel(@RequestBody ChannelCreateRequest channelCreateRequest) {
        if (channelCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long masterId = channelCreateRequest.getMasterId();
        long workspaceId = channelCreateRequest.getWorkspaceId();
        String channelName = channelCreateRequest.getChannelName();

        if (StringUtils.isAnyBlank( String.valueOf(masterId), String.valueOf(workspaceId), channelName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Channel channel = channelService.CreateNewChannel(masterId,workspaceId,channelName);
        return ResultUtils.success(channel);
    }

    @PostMapping("/UpdateChannel")
    public BaseResponse<Long> UpdateChannel(@RequestBody ChannelEditRequest channelEditRequest) {
        if (channelEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = channelEditRequest.getId();
        long workspaceId = channelEditRequest.getWorkspaceId();
        String channelName = channelEditRequest.getChannelName();

        if (StringUtils.isAnyBlank( String.valueOf(id), String.valueOf(workspaceId), channelName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long UpdateChannel = channelService.UpdateChannel(id, workspaceId, channelName);
        return ResultUtils.success(UpdateChannel);
    }

    @PostMapping("/userorgchannel")
    public BaseResponse<Long> createNewUserOrgChannel(@RequestBody UserOrgCreateRequest userOrgCreateRequest) {
        if (userOrgCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId =userOrgCreateRequest.getUserId();
        long workspaceId = userOrgCreateRequest.getWorkspaceId();

        if (StringUtils.isAnyBlank( String.valueOf(userId), String.valueOf(workspaceId))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(channelService.createNewUserOrgChannel(userId,workspaceId));
    }

    @PostMapping("/masterId")
    public BaseResponse<Long> GetCurrentChannelMasterId(@RequestBody ChannelMemberRequest channelMemberRequest) {
        if (channelMemberRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long channelId =channelMemberRequest.getChannelId();

        if (StringUtils.isAnyBlank( String.valueOf(channelId))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(channelService.GetChannelMasterId(channelId));
    }
}
