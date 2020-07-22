package com.dayi.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 * 
 * @author YuKaiFan<yukf @ pvc123.com>
 * @date 2020/7/22 15:37
 */
@Configuration
public class ThreadPoolExecutorConfig {

    /**
     * 配置ThreadPoolTaskExecutor
     * 
     * @return
     */
    @Bean(name = "defaultThreadPool")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        // 核心线程数
        pool.setCorePoolSize(5);
        // 最大线程数
        pool.setMaxPoolSize(10);
        // 队列最大任务数
        pool.setQueueCapacity(50);
        // 线程最大空闲时间
        pool.setKeepAliveSeconds(60);
        // 允许空闲线程退出直到0
        pool.setAllowCoreThreadTimeOut(true);
        // 拒绝策略：调用者运行
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程前缀
        pool.setThreadNamePrefix("ThreadPool-");
        // 初始化
        pool.initialize();

        return pool;
    }

}
