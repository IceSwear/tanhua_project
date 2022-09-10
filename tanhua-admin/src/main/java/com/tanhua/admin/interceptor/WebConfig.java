package com.tanhua.admin.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @Description: 拦截器的注册 实现WebMvcConfigurer
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;


    /**
     * 注册拦截器
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**")  //拦截所有的请求
                .excludePathPatterns(new String[]{"/system/users/login", "/system/users/verification", "/v2/**", "/webjars/**", "/swagger-ui.html", "/swagger-resources/**"});
    }
}
