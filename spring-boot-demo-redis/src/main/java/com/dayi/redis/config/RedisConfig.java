package com.dayi.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/20 17:40
 */
@Configuration
public class RedisConfig {

    /**
     * 配置Redis连接工厂
     * 
     * @return
     */
    @Bean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 设置最大空闲数、最大连接数、最大等待毫秒值
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxWaitMillis(2000L);
        // Jedis连接工厂
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
        // 配置Redis连接服务器
        RedisStandaloneConfiguration rsc = connectionFactory.getStandaloneConfiguration();
        rsc.setHostName("127.0.0.1");
        rsc.setPort(6379);

        return connectionFactory;
    }

    /**
     * 配置RedisTemplate
     * 
     * @param connectionFactory
     *            Redis连接工厂
     * @return
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // RedisTemplate会自动初始化StringRedisSerializer
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        // 设置String、Hash的键值对序列化方式
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);

        return redisTemplate;
    }
}
