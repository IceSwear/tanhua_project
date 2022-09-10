package com.tanhua.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.commons.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:  认证过滤器，主要是Token的校验
 * @Author: Spike Wong
 * @Date: 2022/8/27
 */
@Component
@Slf4j
@RefreshScope
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${gateway.excludedUrls}")
    private List<String> excludedUrls;

    @Value("${gateway.swaggerUrls}")
    private List<String> swaggerUrls;


    /**
     * 过滤规则
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.排除不需要校验的链接
        String path = exchange.getRequest().getURI().getPath();
        log.info("请求路径path:{}",path);
        log.info("gateway.excludedUrls:{},-----gateway.swaggerUrls:{}",excludedUrls,swaggerUrls);
        //排除swagger
        if (isAllowPath(path)){
            //先确认是不是swagger的
            return chain.filter(exchange);
        }
        if (excludedUrls.contains(path)) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("gateway校验:token:{}",token);
        if (!StringUtils.isEmpty(token)) {
            token=token.replaceFirst("Bearer ", "");
        }
        log.info("处理后的token:{}",token);
        //校验是否异常
        boolean verifyToken = JwtUtils.verifyToken(token);
        log.info("认证后的boolean值:{}",verifyToken);
        //
        if (!verifyToken) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", 401);
            responseData.put("errMessage", "用户未登录");
            return responseError(exchange.getResponse(), responseData);
        }
        //
        return chain.filter(exchange);
    }


    /**
     * 顺序
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 响应错误数据
     * @param response
     * @param responseData
     * @return
     */
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        // 将信息转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    /**
     * 遍历白名单
     * @param path
     * @return
     */
    private boolean isAllowPath(String path) {
        //遍历白名单
        for (String allowPath : excludedUrls) {
            //判断是否允许
            if(path.startsWith(allowPath)){
                return true;
            }
        }
        return  false;
    }

}
