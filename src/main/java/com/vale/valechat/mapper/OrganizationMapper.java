package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.vale.valechat.model.entity.Organization;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【organization】的数据库操作Mapper
* @createDate 2023-04-16 17:41:41
* @Entity generator.domain.Organization
*/
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    List<OrganizationVo> getOrganizationInWorkspace(@Param(Constants.WRAPPER) QueryWrapper<OrganizationVo> queryWrapper, long workspaceId);
}




