package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vale.valechat.model.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {
}
