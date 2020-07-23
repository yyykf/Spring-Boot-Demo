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

import java.util.Date;

/**
 * 用户控制器测试类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/23 14:33
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * 测试id查询
     */
    @Test
    public void testGetUser() throws Exception {
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON)
                .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // @formatter:on
    }

    /**
     * 测试查询所有
     */
    @Test
    public void testListUsers() throws Exception {
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.get("/users/all")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // @formatter:on
    }

    /**
     * 测试添加用户
     */
    @Test
    public void testAddUser() throws Exception {
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .param("name", "test1")
                .param("password", "test2")
                .param("salt", "test3")
                .param("email", "test4")
                .param("phoneNumber", "test5")
                .param("status", "1")
                .param("createTime", new Date().toString())
                .param("lastLoginTime", new Date().toString())
                .param("lastUpdateTime", new Date().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // @formatter:on
    }

    /**
     * 测试更新用户
     */
    @Test
    public void testUpdateUser() throws Exception {
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.put("/users")
                .param("id", "1")
                .param("name", "newName")
                .param("lastUpdateTime", new Date().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // @formatter:on
    }

    /**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser() throws Exception{
        // @formatter:off
        mvc.perform(MockMvcRequestBuilders.delete("/users")
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string("true"))
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // @formatter:on
    }
}
