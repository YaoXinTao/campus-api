package com.campus.api.config;

import com.campus.api.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        // 用户相关
                        "/api/v1/mini/user/login",
                        "/api/v1/mini/user/phone-login",
                        "/api/v1/mini/user/send-code",
                        "/api/v1/admin/user/login",
                        "/api/v1/admin/user/create",
                        
                        // 分类相关
                        "/api/v1/mini/category/list",
                        "/api/v1/mini/category/featured",
                        "/api/v1/mini/category/sub/**",
                        
                        // 商品相关
                        "/api/v1/mini/product/list",
                        "/api/v1/mini/product/category/**",
                        "/api/v1/mini/product/search",
                        "/api/v1/mini/product/featured",
                        "/api/v1/mini/product/hot",
                        "/api/v1/mini/product/**",
                        "/api/v1/mini/product/sku/**",

                        // 轮播图相关
                        "/api/v1/mini/banners/list",
                        "/api/v1/mini/banners/**"
                );
    }
} 