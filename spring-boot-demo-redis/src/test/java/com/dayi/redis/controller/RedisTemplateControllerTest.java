package com.dayi.redis.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * RedisTempalte 控制器测试类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/21 14:10
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RedisTemplateControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTemplateControllerTest.class);

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * 测试RedisCallback
     */
    @Test
    public void testRedisCallback() throws Exception {
        // String result = mvc.perform(MockMvcRequestBuilders.get("/template/redisCallback")
        String result = mvc.perform(MockMvcRequestBuilders.get("/template/sessionCallback")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string(RedisTemplateController.SUCCESS))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        LOGGER.info("执行结果：{}", result);
    }

    /**
     * 测试常用操作、事务、流水线
     */
    @Test
    public void testOps() throws Exception {
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
    }
}
