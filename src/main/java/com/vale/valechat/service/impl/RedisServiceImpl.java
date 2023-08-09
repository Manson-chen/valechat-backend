package com.vale.valechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vale.valechat.mapper.UserMapper;
import com.vale.valechat.model.entity.User;
import com.vale.valechat.service.RedisService;
import com.vale.valechat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    UserMapper userMapper;

//    @Scheduled(fixedRate = 5000)
    @Override
    public void checkAndRemoveExpiredData() {
//        SetOperations setOperations = redisTemplate.opsForSet();
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> userList = userMapper.selectList(queryWrapper);
//        for (User user : userList) {
//            String redisKey = String.format("valechat:user:unread:%s", user.getId());
//            Long size = setOperations.size(redisKey);
//            if (size == null || size <= 0) {
//                Long removedCount = setOperations.remove(redisKey);
//                if (removedCount != null && removedCount > 0) {
//                    log.info("Remove expired unread set: " + redisKey);
//                }
//            }
//        }
    }
}
