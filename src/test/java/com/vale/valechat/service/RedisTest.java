package com.vale.valechat.service;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.vale.valechat.common.ErrorCode;
import com.vale.valechat.common.ResultUtils;
import com.vale.valechat.exception.BusinessException;
import com.vale.valechat.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 增
        valueOperations.set("yupiString", "dog");
        valueOperations.set("yupiInt", 1);
        valueOperations.set("yupiDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUserName("yupi");
        valueOperations.set("yupiUser", user);

        // 查
        Object yupi = valueOperations.get("yupiString");
        Assertions.assertTrue("dog".equals((String) yupi));
        yupi = valueOperations.get("yupiInt");
        Assertions.assertTrue(1 == ((Integer) yupi));
        yupi = valueOperations.get("yupiDouble");
        Assertions.assertTrue(2.0 == ((Double) yupi));
        System.out.println(valueOperations.get("yupiUser"));
        redisTemplate.delete("yupiString");
    }

    @Test
    void testUnread(){
        // redis 中将消息加入未读消息列表
        long receiverId = 1l;
        long senderId = 4l;
        int msgType = 0;
        String redisKey = String.format("valechat:user:unread:%s", receiverId);
        Set<String> unreadSet = new HashSet<>();
        String value = null;
        if (msgType == 0) {
            value = "user";
        }else {
            value = "channel";
        }

        SetOperations setOperations = redisTemplate.opsForSet();
        Object unread = setOperations.pop(redisKey);
        // 获取列表是否存在
        if (unread == null) {
            unreadSet.add(value + senderId);
        } else {
            unreadSet = (Set<String>) unread;
            for (String s : unreadSet) {
                System.out.println(s);
            }
            unreadSet.add(value + senderId);
        }
        Long add = setOperations.add(redisKey, unreadSet);

        System.out.println(add);


    }

    @Test
    void testMarkRead(){
        int msgType = 0;
        long senderId = 2;
        long userId = 1;

        boolean result;
        SetOperations setOperations = redisTemplate.opsForSet();
        String redisKey = String.format("valechat:user:unread:%s", userId);
        Set<String> unreadSet = (Set<String>) setOperations.pop(redisKey);

        for (String s : unreadSet) {
            System.out.println(s);
        }

        if (unreadSet == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if(msgType == 0){
            result = unreadSet.remove("user" + senderId);
        } else{
            result = unreadSet.remove("channel" + senderId);
        }
        if (unreadSet != null) {
            setOperations.add(redisKey, unreadSet);
        }
        System.out.println(result);
    }
}
