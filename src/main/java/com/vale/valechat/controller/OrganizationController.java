package com.vale.valechat.controller;

import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.entity.Organization;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.OrganizationService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;

    @GetMapping("/list")
    public BaseResponse<List<Organization>> getOrganizationList(){
        List<Organization> organizationList = organizationService.list();
        System.out.println(organizationList);
        if (organizationList == null || organizationList.isEmpty()){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(organizationList);
    }

    @GetMapping("/id")
    public BaseResponse<Organization> getOrganizationById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Organization organization = organizationService.getById(id);
        return ResultUtils.success(organization);
    }

    @PostMapping("/id/member")
    public BaseResponse<List<UserVO>> getOrganizationMembers(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userVOList = organizationService.GetOrganizationMembers(id);
        return ResultUtils.success(userVOList);
    }
}
