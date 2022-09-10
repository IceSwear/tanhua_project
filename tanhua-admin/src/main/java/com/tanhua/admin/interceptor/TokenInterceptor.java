package com.tanhua.admin.interceptor;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Description: 自定义拦截器
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {


    /**
     * 前置处理
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("请求requestURI:{}", requestURI);
        //1、获取头信息
        String token = request.getHeader("Authorization");
        //app端还加了一个"Bearer "字段
        token = token.replace("Bearer ", "");
        log.info("Token为:{}", token);
        //2、调用service根据token查询用户
        Claims claims = JwtUtils.getClaims(token);
        Admin admin = new Admin();
        admin.setId(claims.get("id", Long.class));
        admin.setUsername(claims.get("username", String.class));
        //4、将对象存入Threadlocal
        AdminHolder.set(admin);
        return true;
    }


    /**
     * 后置处理，删除token
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("ThreadLocal数据清除......");
        AdminHolder.remove();
    }
}
