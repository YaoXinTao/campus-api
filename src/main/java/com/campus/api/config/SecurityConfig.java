package com.campus.api.config;

import com.campus.api.security.JwtAuthenticationEntryPoint;
import com.campus.api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(authorize -> authorize
                // 开放轮播图接口 - 放在最前面优先匹配
                .requestMatchers(
                    "/api/v1/mini/banners/list",
                    "/api/v1/mini/banners/**"
                ).permitAll()
                // Swagger UI v3 (OpenAPI)
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/webjars/**",
                    "/doc.html",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security"
                ).permitAll()
                // 开放登录接口
                .requestMatchers(
                    "/api/v1/mini/user/login",
                    "/api/v1/mini/user/phone-login",
                    "/api/v1/mini/user/send-code",
                    "/api/v1/admin/user/login",
                    "/api/v1/admin/user/create"
                ).permitAll()
                // 开放用户注册相关接口
                .requestMatchers(
                    "/api/v1/mini/user/check-phone",
                    "/api/v1/mini/user/check-student"
                ).permitAll()
                // 开放分类相关接口
                .requestMatchers(
                    "/api/v1/mini/category/list",
                    "/api/v1/mini/category/featured",
                    "/api/v1/mini/category/sub/**"
                ).permitAll()
                // 开放商品相关接口
                .requestMatchers(
                    "/api/v1/mini/product/list",
                    "/api/v1/mini/product/category/**",
                    "/api/v1/mini/product/search",
                    "/api/v1/mini/product/featured",
                    "/api/v1/mini/product/hot",
                    "/api/v1/mini/product/**",
                    "/api/v1/mini/product/sku/**"
                ).permitAll()
                // 开放文件上传接口
                .requestMatchers(
                    "/api/v1/mini/file/upload",
                    "/api/v1/admin/upload"
                ).authenticated()
                // 开放静态资源
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/static/**",
                    "/*.html",
                    "/*.css",
                    "/*.js",
                    "/favicon.ico"
                ).permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
} 