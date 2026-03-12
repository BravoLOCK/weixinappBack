package com.xiaowei.demo.config;

import com.xiaowei.demo.interceptor.JwtInterceptor;
import com.xiaowei.demo.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePaths = new ArrayList<>();
        excludePaths.add("/auth/login");
        excludePaths.add("/auth/register");
        excludePaths.add("/upload/**");
        //excludePaths.add("/error");

        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePaths);

        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**");


    }
}
