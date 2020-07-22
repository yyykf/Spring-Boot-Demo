package com.dayi.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis配置类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/20 17:40
 */
@Configuration
public class RedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 配置RedisTemplate
     * 
     * @param connectionFactory
     *            Redis连接工厂，SpringBoot自动装配
     * @return
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        LOGGER.info(">>>>>>>>>>>RedisTemplate注入成功");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // RedisTemplate会自动初始化StringRedisSerializer
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        // 设置String、Hash的键序列化方式
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);

        return redisTemplate;
    }
}
