package com.dayi.redis.service.impl;

import com.dayi.redis.dao.UserMapper;
import com.dayi.redis.model.User;
import com.dayi.redis.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户Service实现类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/23 11:03
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> listAllUsers() {
        return userMapper.listAllUsers();
    }

    @Override
    @Cacheable(value = "redisCache", key = "'com:dayi:userService:userId:' + #id")
    public User getUserById(Long id) {
        // 先从缓存中查，未命中再查数据库，并放入缓存
        return userMapper.getUserById(id);
    }

    @Override
    @CachePut(value = "redisCache", key = "'com:dayi:userService:userId:' + #id")
    public User addUser(User user) {
        // 插入成功后取用户id加Redis的Key前缀作为 Key，缓存当前用户
        userMapper.addUser(user);
        return user;
    }

    @Override
    @CachePut(value = "redisCache", key = "'com:dayi:userService:userId:' + #id", condition = "#result != null")
    public User updateUser(User user) {
        // 如果原用户存在，更新用户后更新缓存
        // 这里的自调用缓存失效，不用担心读取到缓存中的旧数据
        User originalUser = this.getUserById(user.getId());
        if (null == originalUser) {
            return null;
        }

        userMapper.updateUser(user);
        return originalUser;
    }

    @Override
    @CacheEvict(value = "redisCache", key = "'com:dayi:userService:userId:' + #id", beforeInvocation = false)
    public boolean deleteById(Long id) {
        // 方法执行后移除缓存
        return 1 == userMapper.deleteById(id);
    }
}
