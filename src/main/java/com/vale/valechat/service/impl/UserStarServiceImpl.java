package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.user.UserStarRequest;
import com.vale.valechat.model.entity.UserStar;
import com.vale.valechat.mapper.UserStarMapper;
import com.vale.valechat.service.UserStarService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author jiandongchen
* @description 针对表【user_star】的数据库操作Service实现
* @createDate 2023-05-09 15:11:35
*/
@Service
public class UserStarServiceImpl extends ServiceImpl<UserStarMapper, UserStar>
    implements UserStarService{

    @Resource
    UserStarMapper userStarMapper;

    @Override
    public boolean userStar(UserStarRequest userStarRequest) {
        Long userId = userStarRequest.getUserId();
        Integer starType = userStarRequest.getStarType();
        Long starredId = userStarRequest.getStarredId();
        Long workspaceId = userStarRequest.getWorkspaceId();
        if (userId == null || starType== null || starredId == null || workspaceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("starred_id",starredId)
                .eq("workspace_id", workspaceId);
        List<UserStar> userStarList = userStarMapper.selectList(queryWrapper);
        for (UserStar userStar : userStarList) {
            long userStarType = userStar.getStarType();
            if(userStarType == 1 || userStarType == 2){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "User has starred it");
            }
        }
        UserStar userStar = new UserStar();
        BeanUtils.copyProperties(userStarRequest, userStar);
        int insert = userStarMapper.insert(userStar);
        if (insert <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "insert into database failed");
        }
        return true;
    }

    @Override
    public boolean userDeleteStar(UserStarRequest userStarRequest) {
        Long userId = userStarRequest.getUserId();
        Integer starType = userStarRequest.getStarType();
        Long starredId = userStarRequest.getStarredId();
        Long workspaceId = userStarRequest.getWorkspaceId();
        if (userId == null || starType== null || starredId == null || workspaceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("star_type", starType)
                .eq("starred_id",starredId)
                .eq("workspace_id", workspaceId);
        Integer starNum = userStarMapper.selectCount(queryWrapper);
        if (starNum <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "User didn't star it");
        }
        int deleteNum = userStarMapper.delete(queryWrapper);
        if (deleteNum < 1){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Delete in database failed");
        }
        return true;
    }
}




