package com.abhishek.tenanthub.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * Thread pool
 * </p>
 *
 * @author WangChenChen
 * @version 1.0.1
 * @date 2023/11/09 16:52
 */

@Slf4j
@Configuration
public class TaskExecutorConfiguration {

    @Bean
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("task-");
        // When no threads are free, the calling thread runs the task
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


}
