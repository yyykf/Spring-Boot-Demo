package com.dayi.redis.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis消息监听器
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/22 14:59
 */
@Component
public class RedisMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageListener.class);

    /**
     * 处理消息
     * 
     * @param message
     *            消息，含有消息体和渠道名称
     * @param pattern
     *            渠道名称，等价于 message.getChannel()
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        String bodyStr = new String(body);
        LOGGER.info("body数组：{}, body字符串：{}", body, bodyStr);

        byte[] channel = message.getChannel();
        String channelStr = new String(channel);
        LOGGER.info("channel数组：{}, channel字符串：{}", channel, channelStr);

        String patternStr = new String(pattern);
        LOGGER.info("渠道名称：{}", patternStr);
    }
}
