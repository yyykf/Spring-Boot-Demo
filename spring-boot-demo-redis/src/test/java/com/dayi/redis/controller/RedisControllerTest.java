package com.dayi.redis.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * RedisTemplate 控制器测试类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/21 14:10
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RedisControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisControllerTest.class);

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RedisTemplate redisTemplate;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * 测试RedisCallback
     */
    @Test
    public void testRedisCallback() throws Exception {
        // @formatter:off
        // String result = mvc.perform(MockMvcRequestBuilders.get("/template/redisCallback")
        String result = mvc.perform(MockMvcRequestBuilders.get("/template/sessionCallback")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string(RedisController.SUCCESS))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
        // @formatter:on

        LOGGER.info("执行结果：{}", result);
    }

    /**
     * 测试常用操作、事务、流水线
     */
    @Test
    public void testOps() throws Exception {
        // @formatter:off
        // mvc.perform(MockMvcRequestBuilders.get("/template/string")
        // mvc.perform(MockMvcRequestBuilders.get("/template/hash")
        // mvc.perform(MockMvcRequestBuilders.get("/template/list")
        // mvc.perform(MockMvcRequestBuilders.get("/template/set")
        // mvc.perform(MockMvcRequestBuilders.get("/template/zset")
        // mvc.perform(MockMvcRequestBuilders.get("/template/multi")
        mvc.perform(MockMvcRequestBuilders.get("/template/pipline")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print());
        // @formatter:on
    }

    /**
     * 测试Redis发布和订阅
     */
    @Test
    public void testPubAndSub() throws InterruptedException {
        // 也可以在Redis客户端使用命令 publish topic1 msg
        redisTemplate.convertAndSend("topic1", "Publish A Message");
        // 睡眠2S，让日志打印完整
        TimeUnit.SECONDS.sleep(2L);
    }

    /**
     * 测试Redis 执行Lua脚本，无参数
     */
    @Test
    public void testLuaWithoutArgs() throws Exception {
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.get("/template/luaWithoutArgs").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print());
        // @formatter:on
    }

    /**
     * 测试Redis 执行Lua脚本，有参数
     */
    @Test
    public void testLuaWithArgs() throws Exception {
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.get("/template/luaWithArgs")
                .param("key1", "luaKey1")
                .param("key2", "luaKey2")
                .param("value1", "value")
                .param("value2", "value")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print());
        // @formatter:on
    }
}
