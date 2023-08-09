package com.vale.valechat.controller;

import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.workspace.WorkSpaceVOList;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.UserWorkspaceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/userworkspace")
public class UserWorkspaceController {
    @Resource
    UserWorkspaceService userWorkspaceService;

    @PostMapping("/member")
    public BaseResponse<List<UserVO>> getWorkSpaceMembers(@RequestBody WorkSpaceVOList workSpaceVOList) {
        if (workSpaceVOList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long workspaceId =  workSpaceVOList.getWorkspaceId();
        if ((StringUtils.isAnyBlank(String.valueOf(workspaceId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> workSpaceMemberList = userWorkspaceService.GetWorkSpaceMembers(workspaceId);
        return ResultUtils.success(workSpaceMemberList);
    }

    @PostMapping("/organization")
    public BaseResponse<List<OrganizationVo>> getWorkSpaceOrganizations(@RequestBody WorkSpaceVOList workSpaceVOList) {
        if (workSpaceVOList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long workspaceId =  workSpaceVOList.getWorkspaceId();
        if ((StringUtils.isAnyBlank(String.valueOf(workspaceId)))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<OrganizationVo> workSpaceMemberList = userWorkspaceService.GetWorkSpaceOrganizations(workspaceId);
        return ResultUtils.success(workSpaceMemberList);
    }
}
