package com.tanhua.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 跨域支持
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        //添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        //允许的方法
        config.addAllowedMethod("*");
        //允许的域
        config.addAllowedOrigin("*");
        //允许的请求头
        config.addAllowedHeader("*");
        //添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        //返回新的CorsFilter
        return new CorsWebFilter(source);
    }
}