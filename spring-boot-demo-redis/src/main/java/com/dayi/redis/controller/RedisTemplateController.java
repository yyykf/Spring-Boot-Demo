package com.dayi.redis.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * RedisController 的主控制器
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/21 11:13
 */
@RestController
@RequestMapping("/template")
public class RedisTemplateController {

    public static final String SUCCESS = "SUCCESS";

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * RedisCallback接口，可以在同一连接下执行多条命令
     * 
     * @return 成功
     */
    @GetMapping("/redisCallback")
    public String redisCallback() {
        redisTemplate.execute((RedisCallback<Object>)redisConnection -> {
            redisConnection.set("key1".getBytes(), "value".getBytes());
            redisConnection.hSet("myHash".getBytes(), "field1".getBytes(), "value".getBytes());

            return null;
        });

        return SUCCESS;
    }

    /**
     * SessionCallback接口
     * 
     * @return 成功
     */
    @GetMapping("/sessionCallback")
    public String sessionCallback() {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForValue().set("key2", "value");
                redisOperations.opsForHash().put("myHash", "field2", "value");

                return null;
            }
        });

        return SUCCESS;
    }

    @GetMapping("/string")
    public String testStringOps() {
        return (String)redisTemplate.opsForValue().get("");
    }
}
