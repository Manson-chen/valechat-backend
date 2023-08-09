package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.vale.valechat.model.entity.UserWorkspace;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【user_workspace】的数据库操作Service
* @createDate 2023-04-05 19:33:58
*/
public interface UserWorkspaceService extends IMppService<UserWorkspace> {
    List<UserVO> GetWorkSpaceMembers(Long workspaceId);

    List<OrganizationVo> GetWorkSpaceOrganizations(Long workspaceId);
}
