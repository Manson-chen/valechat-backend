package com.vale.valechat;

import com.vale.valechat.mapper.UserMapper;
import com.vale.valechat.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class ValechatBackendApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetAll(){
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
    }

}
