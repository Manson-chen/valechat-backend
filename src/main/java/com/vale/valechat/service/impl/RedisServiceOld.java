package com.vale.valechat.service.impl;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RedisServiceOld {
    @Resource
    private final JedisPool jedisPool;

    public RedisServiceOld(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public void setHash(String key, Map<String, String> map) {  //key: user Id, value: k-channelId, V- last read time
        // try()里面新建jedis对象可以自动关闭资源，不用jedis.close()
        try (Jedis jedis = jedisPool.getResource()) {
            String keyStr = String.valueOf(key);
            Map<String, String> valueStrMap = new HashMap<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String field = String.valueOf(entry.getKey());
                String val = String.valueOf(entry.getValue());
                valueStrMap.put(field, val);
            }
            jedis.hmset(keyStr, valueStrMap);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public Date getLastReadTime(String userId, String chatId) {
        Jedis jedis = jedisPool.getResource();
        try {
            String lastReadTimeStr = jedis.hget(userId, chatId);
            if (lastReadTimeStr != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date =  dateFormat.parse(lastReadTimeStr);
                return date;
            }
        }
        catch (ParseException e) {
            System.out.println("not found!");
            // Handle exception
        } finally {
            jedis.close();
        }

        // If the user has not read any messages in the channel, return null
        return null;
    }

//    public void markChannelAsRead(Long userId, String channelId) {
//        // Remove the channelId from the list of unread channels for the user
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.lrem(userId + ":unread_channels", 0, channelId);
//        }
//    }
//
//    public void markAllChannelsAsRead(Long userId) {
//        // Remove all elements from the list of unread channels for the user
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.del(userId + ":unread_channels");
//        }
//    }
//
//    public List<String> getUnreadChannels(Long userId) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            return jedis.lrange(userId + ":unread_channels", 0, -1);
//        }
//    }
//
//    public void addUnreadChannel(Long userId, String channelId) {
//        // Add the channelId to the end of the list of unread channels for the user
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.rpush(userId + ":unread_channels", channelId);
//        }
//    }
}
