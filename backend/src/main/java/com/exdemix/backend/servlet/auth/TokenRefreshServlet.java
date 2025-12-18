// TokenRefreshServlet.java - 新增令牌刷新Servlet
package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.util.SecurityUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.TokenRefreshResponseVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/api/auth/refresh")
public class TokenRefreshServlet extends HttpServlet {
    
    private final AuthService authService;
    private final Gson gson;
    
    public TokenRefreshServlet() {
        this.authService = new AuthServiceImpl();
        this.gson = GsonUtil.getGson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // 1. 获取客户端信息
            String clientIp = SecurityUtil.getClientIp(request);
            
            // 2. 获取刷新令牌
            String refreshToken = extractRefreshToken(request);
            
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new com.exdemix.backend.exception.BusinessException("未提供刷新令牌");
            }
            
            // 3. 调用服务层刷新令牌
            TokenRefreshResponseVO refreshResponse = authService.refreshToken(refreshToken, clientIp);
            
            // 4. 返回成功响应
            ApiResponse<TokenRefreshResponseVO> apiResponse = ApiResponse.success(refreshResponse);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
            
            log.info("Token refresh successful");
            
        } catch (com.exdemix.backend.exception.BusinessException e) {
            // 业务异常
            log.warn("Business exception during token refresh: {}", e.getMessage());
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(errorResponse));
            
        } catch (Exception e) {
            // 系统异常
            log.error("System error during token refresh", e);
            ApiResponse<String> errorResponse = ApiResponse.error(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "令牌刷新失败，请重新登录"
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
    
    private String extractRefreshToken(HttpServletRequest request) {
        // 从Authorization头获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 从请求体获取
        try {
            RefreshTokenRequest requestBody = gson.fromJson(request.getReader(), RefreshTokenRequest.class);
            if (requestBody != null && requestBody.getRefreshToken() != null) {
                return requestBody.getRefreshToken();
            }
        } catch (Exception e) {
            // 忽略解析错误
        }
        
        // 从请求参数获取
        String tokenParam = request.getParameter("refresh_token");
        if (tokenParam != null && !tokenParam.trim().isEmpty()) {
            return tokenParam;
        }
        
        return null;
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
    
    // 内部类用于解析刷新令牌请求
    private static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}