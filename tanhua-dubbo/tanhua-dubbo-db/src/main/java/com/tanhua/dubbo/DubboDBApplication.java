package com.tanhua.dubbo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@SpringBootApplication
//mapper扫描
@MapperScan("com.tanhua.dubbo.mappers")
public class DubboDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboDBApplication.class, args);
    }



    //分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
