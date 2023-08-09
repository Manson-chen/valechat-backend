package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vale.valechat.model.entity.OrganizationChannel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author jiandongchen
* @description 针对表【organization_channel】的数据库操作Mapper
* @createDate 2023-04-25 16:48:07
* @Entity generator.domain.OrganizationChannel
*/
public interface OrganizationChannelMapper extends BaseMapper<OrganizationChannel> {

    @Select("SELECT organization_id, channel_id, join_time, is_deleted FROM organization_channel WHERE organization_id = #{organizationId} AND channel_id = #{channelId}")
    OrganizationChannel findOrganizationChannelByIds(@Param("organizationId") Long organizationId, @Param("channelId") Long channelId);

    @Update("UPDATE organization_channel SET is_deleted = 0 WHERE organization_id = #{organizationId} AND channel_id = #{channelId}")
    int updateOrganizationChannelDeleted(@Param("organizationId") Long organizationId, @Param("channelId") Long channelId, @Param("isDeleted") Integer isDeleted);

}




