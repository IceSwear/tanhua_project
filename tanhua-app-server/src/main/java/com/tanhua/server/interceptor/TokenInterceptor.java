package com.tanhua.server.interceptor;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 拦截器，拦截配置
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {


    /**
     * 前置处理，token
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("请求requestURI:{}", requestURI);
        //get token from header 从请求头中获取token
        String token = request.getHeader("Authorization");
        log.info("Token为:{}",token);
        Claims claims = JwtUtils.getClaims(token);
        User user = new User();
        String mobile = (String) claims.get("phone");
        Integer id = (Integer) claims.get("id");
        user.setId(Long.valueOf(id));
        user.setMobile(mobile);
        UserHolder.set(user);
        log.info("ThreadLocal存入User:{}", user);
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
        UserHolder.remove();
    }
}
