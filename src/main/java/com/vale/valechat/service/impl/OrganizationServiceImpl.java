package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.mapper.UserMapper;
import com.vale.valechat.model.entity.Organization;
import com.vale.valechat.mapper.OrganizationMapper;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.vo.UserVO;
import com.vale.valechat.service.OrganizationService;
import com.vale.valechat.util.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author jiandongchen
* @description 针对表【organization】的数据库操作Service实现
* @createDate 2023-04-16 17:41:41
*/
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization>
    implements OrganizationService{
    @Resource
    UserMapper userMapper;
    @Override
    public List<UserVO> GetOrganizationMembers(Long organizationId) {
        QueryWrapper<User> userwrapper = new QueryWrapper<User>()
                .eq("organization_id", organizationId);
        List<UserVO> userVOList = CopyUtil.copyList(userMapper.selectList(userwrapper), UserVO.class);
        return userVOList;
    }
}




