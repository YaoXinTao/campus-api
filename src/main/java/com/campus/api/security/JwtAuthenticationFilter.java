package com.campus.api.security;

import com.campus.api.config.JwtConfig;
import com.campus.api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    // 白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/api/v1/mini/banners/list",
        "/api/v1/mini/product/featured",
        "/api/v1/mini/product/list",
        "/api/v1/mini/category/list"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 检查是否是白名单接口
        String requestPath = request.getRequestURI();
        if (isWhitelisted(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getClaimsFromToken(token);
                String userId = claims.get("userId", String.class);
                String userType = claims.get("userType", String.class);

                // 创建认证信息
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("无法设置用户认证", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getTokenPrefix())) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length()).trim();
        }
        return null;
    }

    /**
     * 检查请求路径是否在白名单中
     */
    private boolean isWhitelisted(String requestPath) {
        return WHITE_LIST.stream()
            .anyMatch(path -> requestPath.equals(path) || requestPath.startsWith(path + "/"));
    }
} 