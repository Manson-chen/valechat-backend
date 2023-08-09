package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.dto.user.UserStarRequest;
import com.vale.valechat.model.entity.UserStar;

/**
* @author jiandongchen
* @description 针对表【user_star】的数据库操作Service
* @createDate 2023-05-09 15:11:35
*/
public interface UserStarService extends IService<UserStar> {

    boolean userStar(UserStarRequest userStarRequest);

    boolean userDeleteStar(UserStarRequest userStarRequest);
}
