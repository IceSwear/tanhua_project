package com.tanhua.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: SwaggerCongig配置
 * @Author: Spike Wong
 * @Date: 2022/9/8
 */
@Slf4j
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private boolean SWAGGER_IS_ENABLE = true;

    /**
     * 创建文档接口信息
     *
     * @return
     */
    public ApiInfo createApiInfo() {
        return new ApiInfoBuilder()
                .title("鸡你太美-交友Admin后端文档")
                .description("陌生人交友")
                .contact(new Contact("ikun探花交友论坛", "tanhua.com", "tanhua@tanhua.com"))
                .version("1.0.0").build();
    }

    /**
     * ioc 容器 存储bean
     *
     * @return
     */
    @Bean
    public Docket createApi() {
        //加token的
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("token").description("token")
                .modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(SWAGGER_IS_ENABLE)
                .apiInfo(createApiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.tanhua.admin.controller"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(pars);//全局带token
        //.apis(RequestHandlerSelectors.basePackage(""))//扫描
        //.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
        //                .paths(PathSelectors.any())
    }
}
