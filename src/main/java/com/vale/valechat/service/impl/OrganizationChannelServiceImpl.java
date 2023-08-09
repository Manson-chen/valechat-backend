package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.vale.valechat.mapper.OrganizationMapper;
import com.vale.valechat.model.entity.Organization;
import com.vale.valechat.model.entity.OrganizationChannel;
import com.vale.valechat.mapper.OrganizationChannelMapper;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.entity.UserChannel;
import com.vale.valechat.model.vo.OrganizationVo;
import com.vale.valechat.service.OrganizationChannelService;
import com.vale.valechat.util.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author jiandongchen
* @description 针对表【organization_channel】的数据库操作Service实现
* @createDate 2023-04-25 16:48:07
*/
@Service
public class OrganizationChannelServiceImpl extends ServiceImpl<OrganizationChannelMapper, OrganizationChannel>
    implements OrganizationChannelService{
    @Resource
    OrganizationChannelMapper organizationChannelMapper;
    @Resource
    OrganizationMapper organizationMapper;
//    @Override
//    public List<OrganizationVo> GetChannelOrganization(long channelId) {
//        List<OrganizationChannel> organizationChannels = organizationChannelMapper.selectList(new QueryWrapper<OrganizationChannel>().eq("channel_id", channelId));
//        Set<Long> organizationIds = new HashSet<>();
//        for (OrganizationChannel organizationChannel : organizationChannels) {
//            organizationIds.add(organizationChannel.getOrganizationId());
//        }
//        List<Organization> organizations = organizationMapper.selectBatchIds(organizationIds);
//        return CopyUtil.copyList(organizations, OrganizationVo.class);
//    }
}




