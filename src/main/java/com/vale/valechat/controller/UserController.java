package com.vale.valechat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vale.valechat.common.BaseResponse;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.controller.threadlocal.UserThreadLocal;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.dto.user.*;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.vo.*;
import com.vale.valechat.oauth.CustomOAuth2UserService;
import com.vale.valechat.service.UserService;
import com.vale.valechat.service.UserStarService;
import com.vale.valechat.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User interface
 *
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Value(("${web.root-path}"))
    private String rootPath;

    @Resource
    private UserService userService;

    @Resource
    private UserStarService userStarService;

    @Resource
    private CustomOAuth2UserService oAuth2UserService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final String ONLINE_USERS_KEY = "valechat:user:online";

    @GetMapping("/oauth/login")
    public BaseResponse<String> oauthLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/oauth2/authorization/google");
        return ResultUtils.success("Using google to login");
    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String userName = userRegisterRequest.getUserName();
        int userRole = userRegisterRequest.getUserRole();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, email, userName) || (userRole < 0 || userRole > 1)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);

    }

    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo Use UserVO to return to the user desensitization view
        User user = userService.userLogin(userAccount, userPassword, request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        String token = JWTUtils.createToken(userVO.getEmail(), userVO.getUserName(), userVO.getUserRole());
        response.setHeader(JWTUtils.TOKEN_HEADER, JWTUtils.TOKEN_PREFIX + token);

        // 维护在线用户列表，用户登录，加入该列表
        long currentTime = System.currentTimeMillis();
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();
        opsForZSet.add(ONLINE_USERS_KEY, String.valueOf(user.getId()), currentTime);
        simpMessagingTemplate.convertAndSend("/online/users", "ONLINE_" + user.getId());
        return ResultUtils.success(userVO);
    }

    /**
     * User logout
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(@RequestBody UserLogoutRequest userLogoutRequest,  HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        Long userId = userLogoutRequest.getUserId();
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();
        opsForZSet.remove(ONLINE_USERS_KEY, String.valueOf(userId));
        simpMessagingTemplate.convertAndSend("/online/users", "OFFLINE_" + userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/online/list")
    public BaseResponse<Set<String>> getOnlineUsers(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime - (60 * 1000);
        opsForZSet.removeRangeByScore(ONLINE_USERS_KEY, 0, expirationTime);
        return ResultUtils.success(opsForZSet.range(ONLINE_USERS_KEY, 0, -1));
    }

    @PostMapping("/heartbeat")
    public BaseResponse<String> handleHeartbeat(@RequestBody UserHeartbeatRequest userHeartbeatRequest, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userHeartbeatRequest.getUserId();
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();
        long currentTime = System.currentTimeMillis();
        opsForZSet.add(ONLINE_USERS_KEY, String.valueOf(userId), currentTime);
        return ResultUtils.success("Heartbeat received");
    }


    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request){
        User user = userService.getCurrentUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/workspace/list")
    public BaseResponse<WorkSpaceListVo> getWorkspaceList(@RequestBody UserWorkSpaceListRequest userWorkSpaceListRequest, HttpServletRequest request){
        if (userWorkSpaceListRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userWorkSpaceListRequest.getUserId();
        if(userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        WorkSpaceListVo workSpaceListVO = userService.getWorkSpaceList(userWorkSpaceListRequest);
        return ResultUtils.success(workSpaceListVO);
    }

    @PostMapping("/chat/list")
    public BaseResponse<ChatListVO> getChatList(@RequestBody UserChatListRequest userChatListRequest, HttpServletRequest request){
        if (userChatListRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userChatListRequest.getUserId();
        long workspaceId = userChatListRequest.getWorkspaceId();
        if(userId <= 0 || workspaceId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ChatListVO chatListVO = userService.getChatList(userChatListRequest);
        return ResultUtils.success(chatListVO);
    }

    @PostMapping("/unreadchat/list")
    public BaseResponse<UnreadChatListVO> getUnreadChatList(@RequestBody UserChatListRequest userChatListRequest, HttpServletRequest request){
        if (userChatListRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userChatListRequest.getUserId();
        long workspaceId = userChatListRequest.getWorkspaceId();
        if(userId <= 0 || workspaceId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UnreadChatListVO chatListVO = userService.getUnreadChatList(userChatListRequest);
        return ResultUtils.success(chatListVO);
    }

    /**
     * Update user profile
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest) {
        User localUser = UserThreadLocal.get();
        if (localUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (userUpdateRequest == null || userUpdateRequest.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", localUser.getEmail());
        User oldUser = userService.getOne(queryWrapper);
        System.out.println(oldUser);
        BeanUtils.copyProperties(userUpdateRequest, oldUser);
        System.out.println(oldUser);
        boolean result = userService.saveOrUpdate(oldUser);
//        User user = new User();
//        BeanUtils.copyProperties(userUpdateRequest, user);
//        boolean result = userService.updateById(user);
        if (!result){
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "User update profile failed");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/avatar/upload")
    public BaseResponse<String> uploadAvatar(@Valid UserAvatarRequest userAvatarRequest, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            String errorMsg = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, errorMsg, "");
        }
        String avatarDoc = "avatar/";
        String oldFileName = userAvatarRequest.getFile().getOriginalFilename(); //获取文件原名
        String newFileName = UUID.randomUUID().toString()
                + oldFileName.substring(oldFileName.lastIndexOf("."));
        String visibleUri = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort()  + "/api/" + avatarDoc + newFileName;
        String saveUri = rootPath + avatarDoc + newFileName;

        log.info("Original image file name={} Image Access Address={} Picture save real address={}",newFileName,visibleUri,saveUri);
        File saveFile = new File(saveUri);

        if(!saveFile.exists()){
            saveFile.mkdirs();
        }
        try {
            userAvatarRequest.getFile().transferTo(saveFile);
            boolean uploadResult = userService.uploadAvatar(userAvatarRequest.getId(), visibleUri);
            if (!uploadResult) {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR);
            }
            return ResultUtils.success(visibleUri);
        }catch (IOException e){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to upload avatar");
        }
    }

    @PostMapping("/star")
    public BaseResponse<Boolean> userStar(@Valid @RequestBody UserStarRequest userStarRequest) {
        if (userStarRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userStarService.userStar(userStarRequest);
        if (!result){
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "User Star failed");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/star/delete")
    public BaseResponse<Boolean> userDeleteStar(@Valid @RequestBody UserStarRequest userStarRequest) {
        if (userStarRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userStarService.userDeleteStar(userStarRequest);
        if (!result){
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "User delete star failed");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/star/list")
    public BaseResponse<ChatListVO> getStarList(@Valid @RequestBody UserStarListRequest userStarListRequest, HttpServletRequest request){
        if (userStarListRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ChatListVO chatListVO = userService.getStarList(userStarListRequest);
        return ResultUtils.success(chatListVO);
    }
}
