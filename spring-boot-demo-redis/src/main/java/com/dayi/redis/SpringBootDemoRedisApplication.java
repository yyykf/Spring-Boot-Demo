package com.dayi.redis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 主启动类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/20 17:29
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.dayi.redis.dao"})
@EnableCaching
public class SpringBootDemoRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoRedisApplication.class, args);
    }
}
