package com.vale.valechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vale.valechat.model.dto.user.UserChatListRequest;
import com.vale.valechat.model.dto.user.UserRegisterRequest;
import com.vale.valechat.model.dto.user.UserStarListRequest;
import com.vale.valechat.model.dto.user.UserWorkSpaceListRequest;
import com.vale.valechat.model.entity.User ;
import com.vale.valechat.model.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-03-15 11:07:04
 */
public interface UserService extends IService<User> {


    /**
     * User registration
     * @param userRegisterRequest
     * @return
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * User login
     *
     * @param userAccount  user account
     * @param userPassword user password
     * @param request Request Information
     * @return User information after desensitization
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * Get Current Login User
     *
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);


    /**
     * User desensitization
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * Get chat list include all Workspace
     * @param userWorkSpaceListRequest
     * @return
     */
    WorkSpaceListVo getWorkSpaceList(UserWorkSpaceListRequest userWorkSpaceListRequest);

    /**
     * Get chat list include all private chat or group chat
     * @param userChatListRequest
     * @return
     */
    ChatListVO getChatList(UserChatListRequest userChatListRequest);

    UnreadChatListVO getUnreadChatList(UserChatListRequest userChatListRequest);

    /**
     * logout
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     *
      * @param id
     * @param saveUri
     * @return
     */
    boolean uploadAvatar(Long id, String saveUri);

    ChatListVO getStarList(UserStarListRequest userStarListRequest);

    UserVO createUserAfterOAuthLoginSuccess(String email, String userName, String avatar);

    UserVO updateUserAfterOAuthLoginSuccess(User user);

    ChatListVO getUserChatList(long workspaceId, long userId);
}
