package com.example.ybook.websocket;

import com.example.ybook.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        // 从查询参数中获取token
        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            String token = extractTokenFromQuery(query);
            if (token != null) {
                try {
                    String username = jwtService.extractUsername(token);
                    
                    // 验证token是否有效（简化验证，不验证UserDetails）
                    if (username != null && !isTokenExpired(token)) {
                        // 将用户信息存储到WebSocket会话属性中
                        attributes.put("username", username);
                        // 这里需要从用户名获取用户ID，为简化暂时设为1L
                        attributes.put("userId", 1L); 
                        return true;
                    }
                } catch (Exception e) {
                    // Token验证失败
                    return false;
                }
            }
        }
        
        return false;
    }

    private boolean isTokenExpired(String token) {
        try {
            String username = jwtService.extractUsername(token);
            return username == null;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后的处理
    }

    private String extractTokenFromQuery(String query) {
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("token=")) {
                return param.substring(6); // 移除 "token=" 前缀
            }
        }
        return null;
    }
}