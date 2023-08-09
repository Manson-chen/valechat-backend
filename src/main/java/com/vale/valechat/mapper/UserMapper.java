package com.vale.valechat.mapper;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserMapper - Interaction with database
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<UserVO> getUserInWorkspace(@Param(Constants.WRAPPER) QueryWrapper<UserVO> queryWrapper, long workspaceId);
}




