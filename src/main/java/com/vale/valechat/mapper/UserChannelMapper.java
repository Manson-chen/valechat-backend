package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.vale.valechat.model.entity.UserChannel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author jiandongchen
* @description 针对表【user_channel】的数据库操作Mapper
* @createDate 2023-04-05 19:35:08
* @Entity generator.domain.UserChannel
*/
public interface UserChannelMapper extends MppBaseMapper<UserChannel> {
    @Select("SELECT user_id, channel_id, join_time, is_deleted FROM user_channel WHERE user_id = #{userId} AND channel_id = #{channelId}")
    UserChannel findUserChannelByIds(@Param("userId") Long userId, @Param("channelId") Long channelId);

    @Update("UPDATE user_channel SET is_deleted = #{isDeleted} WHERE user_id = #{userId} AND channel_id = #{channelId}")
    void updateUserChannelDeleted(@Param("userId") Long userId, @Param("channelId") Long channelId, @Param("isDeleted") Integer isDeleted);
}




