package com.dayi.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/21 10:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemoRedisApplicationTests {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() {}

    /**
     * 测试默认序列化器
     * 
     * 底层通过JDK的ObjectOutputStream和ObjectInputStream来完成序列化和反序列化
     *  ObjectOutputStream objectOutputStream = new ObjectOutputStream(new ByteOutputStream(1024));
     *  objectOutputStream.writeObject("value");
     */
    @Test
    public void testRedisTemplateWithDefaultSerializer() throws IOException {
        // 操作String
        redisTemplate.opsForValue().set("key", "value");
        // 操作Hash
        redisTemplate.opsForHash().put("myHash", "field", "value");
        // 使用StringRedisTemplate来操作字符串
        System.out.println(stringRedisTemplate.opsForValue().get("key"));
    }

}
