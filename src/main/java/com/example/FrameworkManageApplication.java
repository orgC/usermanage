package com.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.framework.common.exception.RestTemplateResponseErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class FrameworkManageApplication {
    @Autowired
    Environment evn;
    private final ObjectMapper objectMapper;

    public FrameworkManageApplication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    @RequestScope
    public RestTemplate restTemplate(HttpServletRequest inReq, RestTemplateBuilder restTemplateBuilder) {
        // 构建restTemplate，请注册异常处理
        final RestTemplate restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler(objectMapper))
                .build();

        // 检查是否有oauth2的认证标识，有就需要传递在header中
        final String authHeader = inReq.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && !authHeader.isEmpty()) {
            restTemplate.getInterceptors().add(
                    (outReq, bytes, clientHttpReqExec) -> {
                        outReq.getHeaders().set(
                                HttpHeaders.AUTHORIZATION, authHeader
                        );
                        return clientHttpReqExec.execute(outReq, bytes);
                    });
        }
        OAuth2AccessTokenResponseHttpMessageConverter oac = new OAuth2AccessTokenResponseHttpMessageConverter();

       //restTemplate.setMessageConverters();
        return restTemplate;
    }


    public static void main(String[] args) {
        SpringApplication.run(FrameworkManageApplication.class, args);
    }

}
