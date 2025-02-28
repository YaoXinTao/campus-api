package com.campus.api.interceptor;

import com.campus.api.config.JwtConfig;
import com.campus.api.util.JwtUtil;
import com.campus.api.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestPath = request.getRequestURI();
        
        // 简化白名单路径检查
        if (requestPath.contains("/mini/product") || 
            requestPath.contains("/mini/category") ||
            requestPath.contains("/mini/user/login") ||
            requestPath.contains("/mini/user/phone-login") ||
            requestPath.contains("/mini/user/send-code")) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader(jwtConfig.getHeader());
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "未登录");
        }

        // 验证token
        token = token.replace(jwtConfig.getTokenPrefix(), "").trim();
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "token已过期或无效");
        }

        return true;
    }
} 