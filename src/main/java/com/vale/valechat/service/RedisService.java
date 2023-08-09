package com.vale.valechat.service;

/**
 * Redis 相关操作的接口
 */
public interface RedisService {

    /**
     * 检查过期的 unread message 列表的数据
     */
    void checkAndRemoveExpiredData();
}
