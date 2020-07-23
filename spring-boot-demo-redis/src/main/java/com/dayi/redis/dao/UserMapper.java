package com.dayi.redis.dao;

import com.dayi.redis.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/23 10:27
 */
public interface UserMapper {

    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    @Select("SELECT * FROM user")
    List<User> listAllUsers();

    /**
     * 根据id查询用户
     * 
     * @param id
     *            用户id
     * @return 存在 - 对应用户，不存在 -  {@code null}
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(Long id);

    /**
     * 添加用户
     * 
     * @param user
     *            待添加用户
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    int addUser(User user);

    /**
     * 更新用户
     * 
     * @param user
     *            待更新用户
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    int updateUser(User user);

    /**
     * 根据id删除用户
     * 
     * @param id
     *            用户id
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
}
