package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.mapper.*;
import com.vale.valechat.model.dto.user.UserChatListRequest;
import com.vale.valechat.model.dto.user.UserRegisterRequest;
import com.vale.valechat.model.dto.user.UserStarListRequest;
import com.vale.valechat.model.dto.user.UserWorkSpaceListRequest;
import com.vale.valechat.model.entity.*;
import com.vale.valechat.model.vo.*;
import com.vale.valechat.service.ChannelService;
import com.vale.valechat.service.UserService;
import com.vale.valechat.util.CopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vale.valechat.constant.UserConstant.USER_LOGIN_STATE;


/**
 * Service implementation of database operation for table [user (user)]
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserStarMapper userStarMapper;

    @Resource
    private UserWorkspaceMapper userWorkspaceMapper;

    @Resource
    private OrganizationWorkspaceMapper organizationWorkspaceMapper;

    @Resource
    private ChannelMapper channelMapper;

    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private WorkspaceMapper workspaceMapper;

    @Resource
    private UserChannelMapper userChannelMapper;

    @Resource
    private ChannelMessageMapper channelMessageMapper;
    @Resource
    private OrganizationChannelMapper organizationChannelMapper;
    @Resource
    private RedisServiceOld redisServiceOld;

    @Resource
    private PrivateMessageMapper privateMessageMapper;

    @Resource
    private ChannelService channelService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 盐值，混淆密码 Salt value, confusing password
     */
    public static final String SALT = "vale";

    /**
     * 用户脱敏 User desensitization
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUserAvatar(originUser.getUserAvatar());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setOrganizationId(originUser.getOrganizationId());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());

        return safetyUser;
    }

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String userName = userRegisterRequest.getUserName();
        int userRole = userRegisterRequest.getUserRole();

        // 1. verification
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, email, userName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter is empty");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The user account is too short.");
        }

        if (userName.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The user name is too short.");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User password is too short");
        }

        if (userRole < 0 || userRole > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userRole is out of range");
        }

//        if ((userRole == 0 && userRegisterRequest.getOrganizationId() != null) || (userRole == 1 && userRegisterRequest.getOrganizationId() == null)){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The userRole is wrong");
//        }

        //Account cannot contain special characters
        String validPattern = "[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account cannot contain special characters");
        }

        //The password is the same as the check password
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Check password error");
        }

        //Accounts cannot be duplicated
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Duplicate account");
        }
        //Email cannot be duplicated
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Duplicate email");
        }
        //Organization is not exist
        if(userRole == 1){
            long organizationId = userRegisterRequest.getOrganizationId();
            QueryWrapper<Organization> orgQueryWrapper = new QueryWrapper<>();
            orgQueryWrapper.eq("id", organizationId);
            count = organizationMapper.selectCount(orgQueryWrapper);
            if (count != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Organization is not exist");
            }
        }
        // 2. encryption
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. Insert data
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setEmail(email);
        user.setUserName(userName);
        user.setUserRole(userRole);
        if (userRole == 1){
            user.setOrganizationId(userRegisterRequest.getOrganizationId());
        }
        boolean saveResult = this.save(user);

        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Insert database error");
        }

        // 4. Add user to all workspaces
        addUserToAllWorkspace(user.getId());

        return user.getId();
    }

    public void addUserToAllWorkspace(long userId){
        List<Workspace> workspaceList = workspaceMapper.selectList(null);
        for (Workspace workspace : workspaceList) {
            Long workspaceId = workspace.getId();
            UserWorkspace userWorkspace = new UserWorkspace();
            userWorkspace.setUserId(userId);
            userWorkspace.setWorkspaceId(workspaceId);
            userWorkspaceMapper.insert(userWorkspace);
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. Verification
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request parameter is empty");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The user account is too short.");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User password is too short");
        }

//        // Account cannot contain special characters
//        String validPattern = "[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
//        if (matcher.find()) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account cannot contain special characters");
//        }

        //2. Encryption
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // Query whether the user exists
        // A data manipulation class is installed to set query conditions
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_account", userAccount).or().eq("email", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        // User does not exist
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR, "Account does not exist");

        }

        //3. User desensitization
        User safetyUser = getSafetyUser(user);

        // 4. Record the login status of the user
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
//        System.out.println("SessionId: " + request.getSession().getId());
//        System.out.println(request.getSession().getAttribute(USER_LOGIN_STATE));
        return safetyUser;
    }


    /**
     * Get Current Login User
     *
     * @param request
     * @return
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        // Judge the login state
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        System.out.println("curId:" + request.getSession().getId());
        if(currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // Query from the database (for performance, you can comment and go directly to the cache)
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return currentUser;
    }

    @Override
    public ChatListVO getUserChatList(long workspaceId, long userId) {

        User user = this.getById(userId);

        // 1. select all users in this workspace
        List<UserVO> userVOList = userMapper.getUserInWorkspace(new QueryWrapper<>(), workspaceId);

//        List<UserVO> userVOList = new ArrayList<>();
//        QueryWrapper<UserWorkspace> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("workspace_id", workspaceId);
//        List<UserWorkspace> userWorkspaceList = userWorkspaceMapper.selectList(queryWrapper);
//        if (userWorkspaceList == null){
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
//        for (UserWorkspace userWorkspace : userWorkspaceList) {
//            long currentUserId = userWorkspace.getUserId();
//            User currentUser = this.getById(currentUserId);
//            UserVO userVO = new UserVO();
//            BeanUtils.copyProperties(currentUser, userVO);
//            userVOList.add(userVO);
//        }

        // 2. select user joined channels and channels in this workspace
        List<Channel> channelList = new ArrayList<>();
        if(user.getUserRole() == 0) {
            channelList = channelMapper.getChannelByUser(userId, workspaceId);
        }else{
            channelList = channelMapper.getChannelByOrg(user.getOrganizationId(), workspaceId);
        }

        //Get all channels in this workspace
        // List<OrganizationVo> organizationVoList = organizationMapper.getOrganizationInWorkspace(new QueryWrapper<>(), workspaceId);
        List<Channel> NormalChannel = new ArrayList<>();
        List<OrgChannelVo> OrganizationChannel = new ArrayList<>();

        for(Channel channel:channelList){
            if(channel.getChannelType()==0){
                NormalChannel.add(channel);
            } else{
                OrgChannelVo orgChannelVo = new OrgChannelVo();
                orgChannelVo.setChannelId(channel.getId());
                orgChannelVo.setChannelType(1);
                if(user.getUserRole() == 0) {
                    QueryWrapper<OrganizationChannel> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("channel_id", channel.getId());
                    Organization organization = organizationMapper.selectById(organizationChannelMapper.selectOne(queryWrapper).getOrganizationId());
                    orgChannelVo.setChatName(organization.getOrganizationName());
                    orgChannelVo.setId(organization.getId());
                    orgChannelVo.setChatEmail(organization.getEmail());
                    orgChannelVo.setChatAvatar(organization.getOrganizationAvatar());  //Get organization's avatar
                }else{
                    QueryWrapper<UserChannel> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("channel_id", channel.getId());
                    User client = userMapper.selectById(userChannelMapper.selectOne(queryWrapper).getUserId());
                    orgChannelVo.setChatName(client.getUserName());
                    orgChannelVo.setId(client.getId());
                    orgChannelVo.setChatAvatar(client.getUserAvatar());
                    orgChannelVo.setChatEmail(client.getEmail());
                }
                OrganizationChannel.add(orgChannelVo);
            }
        }

//        List<Long> channelIds = new ArrayList<>();
//        if(user.getUserRole() == 0) {
//            QueryWrapper<UserChannel> userChannelQueryWrapper = new QueryWrapper<>();
//            userChannelQueryWrapper.eq("user_id", userId);
//            List<UserChannel> userChannels = userChannelMapper.selectList(userChannelQueryWrapper);
//           channelIds = userChannels.stream()
//                    .map(UserChannel::getChannelId)
//                    .collect(Collectors.toList());
//        }else{
//            QueryWrapper<OrganizationChannel> userChannelQueryWrapper = new QueryWrapper<>();
//            userChannelQueryWrapper.eq("organization_id", user.getOrganizationId());
//            List<OrganizationChannel> organizationChannels = organizationChannelMapper.selectList(userChannelQueryWrapper);
//            if (organizationChannels  == null){
//                throw new BusinessException(ErrorCode.NULL_ERROR);
//            }
//            channelIds = organizationChannels.stream()
//                    .map(OrganizationChannel::getChannelId)
//                    .collect(Collectors.toList());
//        }
//        List<Channel> userChannelList = channelMapper.selectBatchIds(channelIds);
//        QueryWrapper<Channel> channelQueryWrapper = new QueryWrapper<>();
//        channelQueryWrapper.eq("workspace_id", workspaceId);
//        channelList = channelMapper.selectList(channelQueryWrapper);
//        channelList.retainAll(userChannelList);

        // 3. 封装ChatListVO返回类
        ChatListVO chatListVO = new ChatListVO();
        chatListVO.setUserList(userVOList);
        chatListVO.setChannelList(NormalChannel);
        chatListVO.setOrgChannelList(OrganizationChannel);
        return chatListVO;
    }

    @Override
    public ChatListVO getChatList(UserChatListRequest userChatListRequest) {
        long userId = userChatListRequest.getUserId();
        long workspaceId = userChatListRequest.getWorkspaceId();

        User user = this.getById(userId);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // redis 缓存列表
        // 如果有缓存直接读缓存
        String redisKey = String.format("valechat:worksapce:%s:user:chatlist:%s",workspaceId, userId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        ChatListVO chatListVO  = (ChatListVO) valueOperations.get(redisKey);
        if (chatListVO != null) {
            return chatListVO;
        }
        // 从数据库中获取，然后写入 redis 缓存
        chatListVO = getUserChatList(workspaceId, userId);
        try {
            valueOperations.set(redisKey, chatListVO, 3600000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set chat list error", e);
        }
        return chatListVO;
    }

    @Override
    public UnreadChatListVO getUnreadChatList(UserChatListRequest userChatListRequest) {
        Long userId = userChatListRequest.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UnreadChatListVO unreadChatListVO = new UnreadChatListVO();
        List<Long> userList = new ArrayList<>();
        List<Long> channelList = new ArrayList<>();
        String redisKey = String.format("valechat:user:unread:%s", userId);
        // 加入集合的时候有问题 用了set 加入
        Set<String> unreadSet = redisTemplate.opsForSet().members(redisKey);
//        redisTemplate.opsForSet().add(redisKey, unreadSet);
        if (unreadSet == null) {
            return unreadChatListVO;
        }
        for (String str : unreadSet) {
            if (str.contains("user")) {
                long senderId = Long.parseLong(str.substring(4));
                userList.add(senderId);
            } else if (str.contains("channel")) {
                long channelId = Long.parseLong(str.substring(7));
                channelList.add(channelId);
            }
        }
        unreadChatListVO.setUserId(userId);
        unreadChatListVO.setUserList(userList);
        unreadChatListVO.setChannelList(channelList);
        return unreadChatListVO;

//        ChatListVO chatListVO = this.getChatList(userChatListRequest);
//        Long userId = userChatListRequest.getUserId();
//        List<UserVO> unreadPrivateChatList = new ArrayList<>();
//        List<Channel> unreadChannelList = new ArrayList<>();
//        List<OrgChannelVo> unreadOrgChannelList = new ArrayList<>();
//
//        for(UserVO userVO : chatListVO.getUserList()){
//            QueryWrapper<PrivateMessage> queryWrapper = new QueryWrapper<>();
//            // 获取mysql数据库中私聊消息最新的那一个
//            queryWrapper.eq("receiver_id", userId).eq("sender_id", userVO.getId()).orderByDesc("create_time").last("LIMIT 1");
//            PrivateMessage message = privateMessageMapper.selectOne(queryWrapper);
//            if(message != null) {
//                Date chatLastMessageTime = message.getCreateTime();
//                Date userLastReadTime = redisServiceOld.getLastReadTime("user" + userId, "u" + userVO.getId());
//                if ((userLastReadTime == null) || userLastReadTime.compareTo(chatLastMessageTime) < 0) {
//                    unreadPrivateChatList.add(userVO);
//                }
//            }
//        }
//        for(Channel channel: chatListVO.getChannelList()){
//            QueryWrapper<ChannelMessage> queryWrapper = new QueryWrapper<>();
//            // ne 有一个排除的作用，自己发的消息不可能未读
//            queryWrapper.eq("channel_id", channel.getId()).ne("sender_id", userId).orderByDesc("create_time").last("LIMIT 1");
//            ChannelMessage message = channelMessageMapper.selectOne(queryWrapper);
//            if(message != null) {
//                Date chatLastMessageTime = message.getCreateTime();
//                Date userLastReadTime = redisServiceOld.getLastReadTime("user" + userId, "c" + channel.getId());
//                if ((userLastReadTime == null) || userLastReadTime.compareTo(chatLastMessageTime) < 0) {
//                    unreadChannelList.add(channel);
//                }
//            }
//        }
//        for(OrgChannelVo orgChannelVo: chatListVO.getOrgChannelList()){
//            QueryWrapper<ChannelMessage> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("channel_id", orgChannelVo.getChannelId()).ne("sender_id", userId).orderByDesc("create_time").last("LIMIT 1");
//            ChannelMessage message = channelMessageMapper.selectOne(queryWrapper);
//            if(message != null) {
//                Date chatLastMessageTime = message.getCreateTime();
//                Date userLastReadTime = redisServiceOld.getLastReadTime("user" + userId, "c" + orgChannelVo.getChannelId());
//                if ((userLastReadTime == null) || userLastReadTime.compareTo(chatLastMessageTime) < 0) {
//                    unreadOrgChannelList.add(orgChannelVo);
//                }
//            }
//        }
//        ChatListVO unreadChatListVO = new ChatListVO();
//        unreadChatListVO.setUserList(unreadPrivateChatList);
//        unreadChatListVO.setChannelList(unreadChannelList);
//        unreadChatListVO.setOrgChannelList(unreadOrgChannelList);
//        return unreadChatListVO;
    }

    @Override
    public WorkSpaceListVo getWorkSpaceList(UserWorkSpaceListRequest userWorkSpaceListRequest) {
        long userId = userWorkSpaceListRequest.getUserId();
        User user = this.getById(userId);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Workspace> workspaceList = new ArrayList<Workspace>();
        Set<Long> workspaceIds = new HashSet<>();
        if(user.getUserRole() == 0){
            List<UserWorkspace> userWorkspace = userWorkspaceMapper.selectList(new QueryWrapper<UserWorkspace>().eq("user_id", userId));
            for (UserWorkspace userworkspace : userWorkspace) {
                workspaceIds.add(userworkspace.getWorkspaceId());
            }
        }
        else if(user.getUserRole()==1){
            List<OrganizationWorkspace> organizationWorkspace = organizationWorkspaceMapper.selectList(new QueryWrapper<OrganizationWorkspace>().eq("organization_id", user.getOrganizationId()));
            for ( OrganizationWorkspace  organizationworkspace :  organizationWorkspace) {
                workspaceIds.add(organizationworkspace.getWorkspaceId());
            }
        }
        workspaceList = workspaceMapper.selectBatchIds(workspaceIds);

        WorkSpaceListVo workSpaceListVo = new WorkSpaceListVo();
        workSpaceListVo.setWorkSpaceList(CopyUtil.copyList(workspaceList, WorkSpaceVo.class));
        return workSpaceListVo;
    }

    /**
     * User logout
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if(request.getSession().getAttribute(USER_LOGIN_STATE) == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN, "The user didn't login");
        }
        // remove the user login session
//        User user  = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean uploadAvatar(Long id, String saveUri) {
        if (id == null || saveUri == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        user.setId(id);
        user.setUserAvatar(saveUri);
        int result = userMapper.updateById(user);
        return result > 0;
    }

    @Override
    public ChatListVO getStarList(UserStarListRequest userStarListRequest) {

        List<UserVO> userVOList = new ArrayList<>();
        List<Channel> normalChannelList = new ArrayList<>();
        List<OrgChannelVo> organizationChannelList = new ArrayList<>();

        Long userId = userStarListRequest.getUserId();
        Long workspaceId = userStarListRequest.getWorkspaceId();

        QueryWrapper<UserStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("workspace_id", workspaceId)
                .orderByDesc("create_time");
        List<UserStar> userStarList = userStarMapper.selectList(queryWrapper);

        for (UserStar userStar:userStarList){
            long starredId = userStar.getStarredId();
            int starType = userStar.getStarType();

            if (starType == 0){
                User user = userMapper.selectById(starredId);
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                userVOList.add(userVO);
            }else if(starType == 1){
                Channel channel = channelMapper.selectById(starredId);
                normalChannelList.add(channel);
            }else if (starType == 2){
                Channel channel = channelMapper.selectById(starredId);
                OrgChannelVo orgChannelVo = new OrgChannelVo();

                QueryWrapper<OrganizationChannel> orgChannelQueryWrapper = new QueryWrapper<>();
                orgChannelQueryWrapper.eq("channel_id", channel.getId());
                OrganizationChannel organizationChannel = organizationChannelMapper.selectOne(orgChannelQueryWrapper);
                Organization organization = organizationMapper.selectById(organizationChannel.getOrganizationId());

                orgChannelVo.setChannelId(channel.getId());
                orgChannelVo.setChatName(organization.getOrganizationName());
                orgChannelVo.setId(organization.getId());

                organizationChannelList.add(orgChannelVo);
            }
        }
        ChatListVO chatListVO = new ChatListVO();
        chatListVO.setUserList(userVOList);
        chatListVO.setChannelList(normalChannelList);
        chatListVO.setOrgChannelList(organizationChannelList);
        return chatListVO;
    }

    @Override
    public UserVO createUserAfterOAuthLoginSuccess(String email, String userName, String avatar) {
        User newUser = new User();
        newUser.setUserAccount(email);
//            newUser.setUserPassword(encryptPassword);
        newUser.setEmail(email);
        newUser.setUserName(userName);
        newUser.setUserAvatar(avatar);
        newUser.setUserRole(0);

        boolean saveResult = this.save(newUser);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Insert database error");
        }
        // 4. Add user to all workspaces
        this.addUserToAllWorkspace(newUser.getId());
        List<Workspace> workspaces = workspaceMapper.selectList(null);
        for (Workspace workspace : workspaces) {
            Long workspaceId = workspace.getId();
            channelService.createNewUserOrgChannel(newUser.getId(), workspaceId);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(newUser, userVO);
        return userVO;
    }

    @Override
    public UserVO updateUserAfterOAuthLoginSuccess(User user) {

        boolean updateStatus = this.updateById(user);
        if (!updateStatus) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Insert database error");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}




