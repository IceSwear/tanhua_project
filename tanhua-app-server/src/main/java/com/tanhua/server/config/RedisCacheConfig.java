package com.tanhua.server.config;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;
import java.util.Map;

/**
 * @Description: Redis缓存统一时间处理
 * @Author: Spike Wong
 * @Date: 2022/8/27
 */
@Configuration
public class RedisCacheConfig {

    private static final Map<String, Duration> cacheMap;

    static {
        //将缓存
        cacheMap = ImmutableMap.<String, Duration>builder()
                .put("videos", Duration.ofSeconds(10L))
                .put("friends_mvm",Duration.ofSeconds(8L))
                .put("my_mvm",Duration.ofMillis(10L))
                .put("announcement",Duration.ofMillis(10L))
                .build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            //根据不同的cachename设置不同的失效时间
            for (Map.Entry<String, Duration> entry : cacheMap.entrySet()) {
                builder.withCacheConfiguration(entry.getKey(), RedisCacheConfiguration.defaultCacheConfig().entryTtl(entry.getValue()));
            }
        };
    }

}
