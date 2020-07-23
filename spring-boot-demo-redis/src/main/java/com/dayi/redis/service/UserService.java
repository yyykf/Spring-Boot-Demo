package com.dayi.redis.service;

import com.dayi.redis.model.User;

import java.util.List;

/**
 * 用户Service
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/23 11:03
 */
public interface UserService {

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> listAllUsers();

    /**
     * 根据id查询用户
     *
     * @param id
     *            用户id
     * @return 存在 - 对应用户，不存在 -  {@code null}
     */
    User getUserById(Long id);

    /**
     * 添加用户
     *
     * @param user
     *            待添加用户，无id
     * @return 含有添加成功后id的用户
     */
    User addUser(User user);

    /**
     * 更新用户
     *
     * @param user
     *            待更新用户
     * @return 对应id存在 - 原始用户 对应id不存在 - {@code null}
     */
    User updateUser(User user);

    /**
     * 根据id删除用户
     *
     * @param id
     *            用户id
     * @return 成功 - {@code true} 失败 - {@code false}
     */
    boolean deleteById(Long id);
}
