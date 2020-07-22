package com.dayi.redis.config;

import com.dayi.redis.listener.RedisMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    /**
     * 配置Redis消息监听容器
     * 
     * @param connectionFactory
     *            Redis连接工厂，SpringBoot自动装配
     * @param redisMessageListener
     *            自定义消息监听器
     * @param taskExecutor
     *            线程池
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       RedisMessageListener redisMessageListener,
                                                                       ThreadPoolTaskExecutor taskExecutor) {
        LOGGER.info(">>>>>>>>>>>RedisMessageListenerContainer注入成功");

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 设置连接工厂
        container.setConnectionFactory(connectionFactory);
        // 设置线程池
        container.setTaskExecutor(taskExecutor);
        // 监听topic1渠道
        Topic topic = new ChannelTopic("topic1");
        container.addMessageListener(redisMessageListener, topic);

        return container;
    }
}
