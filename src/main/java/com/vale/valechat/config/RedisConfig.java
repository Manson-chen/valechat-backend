package com.vale.valechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10); // Maximum idle connections in the pool
        jedisPoolConfig.setMaxTotal(50); // Maximum total connections in the pool
        jedisPoolConfig.setMaxWaitMillis(10000); // Maximum wait time to obtain a connection from the pool
        jedisPoolConfig.setMinIdle(5); // Minimum idle connections in the pool
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
        return new JedisPool(jedisPoolConfig, redisHost, redisPort);
    }
}
