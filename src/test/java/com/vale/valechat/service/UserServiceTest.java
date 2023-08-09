package com.vale.valechat.service;

import com.vale.valechat.model.dto.user.UserChatListRequest;
import com.vale.valechat.model.dto.user.UserRegisterRequest;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.model.vo.ChatListVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * User Service Test
 *
 * @author Jd
 */

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser(){
        User user = new User();
        user.setUserName("jiandong");
        user.setUserAccount("123");
        user.setUserAvatar("");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        // Check that the userPassword and check Password can't be none
        String userAccount = "jiandong123";
        String userPassword = "";
        String checkPassword = "12345678";

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        userRegisterRequest.setCheckPassword(checkPassword);
        long result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1, result);

        // Check character number of the userAccount
        userAccount = "jd";
        userRegisterRequest.setUserAccount(userAccount);
        result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1, result);

        // Check the character number of password
        userAccount = "jiandong123";
        userPassword = "123456";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1, result);

        //Check the special characters of the user account
        userAccount = "jian ?dong";
        userPassword = "12345678";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setUserPassword(userPassword);
        result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1, result);

        // Check the userPassword and checkPassword is same or not
        checkPassword = "123456789";
        userRegisterRequest.setCheckPassword(checkPassword);
        result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1, result);

        // Check the userAccount has been in database or not
        userAccount = "123";
        checkPassword = "12345678";
        userRegisterRequest.setUserAccount(userAccount);
        userRegisterRequest.setCheckPassword(checkPassword);
        result = userService.userRegister(userRegisterRequest);
        Assertions.assertEquals(-1, result);

        // Testing the new user can be insert in database or not
        userAccount = "jiandong123";
        userRegisterRequest.setUserAccount(userAccount);
        result = userService.userRegister(userRegisterRequest);
        Assertions.assertTrue(result > 0);
    }

    // todo
    @Test
    void userLogin() {
    }

    // todo
    @Test
    void getSafetyUser() {
    }

    // todo
    @Test
    void getCurrentUser() {
    }

    @Test
    void getChatList() {
        UserChatListRequest userChatListRequest = new UserChatListRequest();
        userChatListRequest.setUserId(1L);
        userChatListRequest.setWorkspaceId(1L);

        ChatListVO chatListVO = userService.getChatList(userChatListRequest);
        Assertions.assertNotSame(null, chatListVO);
        System.out.println(chatListVO);
    }
}