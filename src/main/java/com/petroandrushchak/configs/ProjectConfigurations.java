package com.petroandrushchak.configs;

import com.petroandrushchak.fut.configs.BrowserConfigs;
import com.petroandrushchak.process.BrowserProcessHelper;
import org.aeonbits.owner.ConfigCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;


@Configuration
public class ProjectConfigurations {

    public static BrowserConfigs browserConfigs() {
        return ConfigCache.getOrCreate(BrowserConfigs.class);
    }

    @Bean
    @Scope("singleton")
    public BrowserProcessHelper browserProcessHelper() {
        return new BrowserProcessHelper();
    }

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Magnificent-");
        executor.initialize();
        return executor;
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}