package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vale.valechat.model.entity.Channel;

import java.util.List;

/**
* @author jiandongchen
* @description 针对表【channel】的数据库操作Mapper
* @createDate 2023-04-05 19:34:49
* @Entity generator.domain.Channel
*/
public interface ChannelMapper extends BaseMapper<Channel> {

    List<Channel> getChannelByUser(long userId, long workspaceId);

    List<Channel> getChannelByOrg(long organizationId, long workspaceId);
}




