package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.entity.Organization;
import com.vale.valechat.model.vo.UserVO;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【organization】的数据库操作Service
* @createDate 2023-04-16 17:41:41
*/
public interface OrganizationService extends IService<Organization> {
    List<UserVO> GetOrganizationMembers(Long channelId);
}
