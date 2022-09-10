package com.tanhua.server.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 注册拦截器
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {


    /**
     * 拦截器注册
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor()).addPathPatterns("/**").excludePathPatterns(new String[]{"/user/login", "/user/loginVerification", "/v2/**", "/webjars/**", "/swagger-ui.html", "/swagger-resources/**"});
    }
}
