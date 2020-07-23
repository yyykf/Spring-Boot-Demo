package com.dayi.redis.controller;

import com.dayi.redis.model.User;
import com.dayi.redis.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户控制器，结合Redis操作Cache
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/23 10:28
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 根据id查询用户
     * 
     * @param id
     *            用户id
     * @return 存在 - 对应用户，不存在 - {@code null}
     */
    @GetMapping
    public User getUser(Long id) {
        return userService.getUserById(id);
    }

    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    @GetMapping({"/all"})
    public List<User> listAllUsers() {
        return userService.listAllUsers();
    }

    /**
     * 添加用户
     * 
     * @param user
     *            带添加用户
     * @return 含有添加成功后id的用户
     */
    @PostMapping
    public User addUser(User user) {
        return userService.addUser(user);
    }

    /**
     * 更新用户
     *
     * @param user
     *            待更新用户
     * @return 对应id存在 - 原始用户 对应id不存在 - {@code null}
     */
    @PutMapping
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    /**
     * 根据id删除用户
     *
     * @param id
     *            用户id
     * @return 成功 - {@code true} 失败 - {@code false}
     */
    @DeleteMapping
    public Boolean deleteUser(Long id) {
        return userService.deleteById(id);
    }
}
