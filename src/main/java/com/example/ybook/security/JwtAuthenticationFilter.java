package com.example.ybook.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <p>
 * JWT 认证过滤器
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final com.example.ybook.service.UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
                                   com.example.ybook.service.UserService userService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            final String username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // 填充 ThreadLocal 的当前用户信息
                    Long userId = userService.lambdaQuery()
                            .eq(com.example.ybook.entity.UserEntity::getUsername, username)
                            .oneOpt()
                            .map(com.example.ybook.entity.UserEntity::getId)
                            .orElse(null);
                    java.util.Set<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(java.util.stream.Collectors.toSet());
                    CurrentUserContext.set(new CurrentUser(userId, username, roles));
                }
            }
        } catch (Exception e) {
            // 在开发阶段记录解析失败，避免完全吞没异常
            log.debug("JWT 解析失败: {}", e.getMessage());
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 防止线程复用导致的 ThreadLocal 泄漏
            CurrentUserContext.clear();
        }
    }
}
