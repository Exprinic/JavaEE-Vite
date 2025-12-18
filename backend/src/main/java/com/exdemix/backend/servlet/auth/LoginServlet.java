// LoginServlet.java - 完整重构
package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.util.SecurityUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.LoginResponseVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {
    
    private final AuthService authService;
    private final Gson gson;
    
    public LoginServlet() {
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
            String userAgent = SecurityUtil.getUserAgent(request);
            
            // 2. 解析请求体
            LoginRequestDTO loginRequest = gson.fromJson(request.getReader(), LoginRequestDTO.class);
            
            // 3. 验证请求数据
            validateLoginRequest(loginRequest);
            
            // 4. 调用服务层登录
            LoginResponseVO loginResponse = authService.login(loginRequest, clientIp, userAgent);
            
            // 5. 设置响应头（安全相关）
            setSecurityHeaders(response);
            
            // 6. 返回成功响应
            ApiResponse<LoginResponseVO> apiResponse = ApiResponse.success(loginResponse);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
            
            log.info("Login successful for phone: {}", loginRequest.getPhone());
            
        } catch (com.exdemix.backend.exception.BusinessException e) {
            // 业务异常
            log.warn("Business exception during login: {}", e.getMessage());
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
            
        } catch (Exception e) {
            // 系统异常
            log.error("System error during login", e);
            ApiResponse<String> errorResponse = ApiResponse.error(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "系统内部错误，请稍后重试"
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
    
    private void validateLoginRequest(LoginRequestDTO request) {
        if (request == null) {
            throw new com.exdemix.backend.exception.BusinessException("请求数据不能为空");
        }
        
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new com.exdemix.backend.exception.BusinessException("手机号不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new com.exdemix.backend.exception.BusinessException("密码不能为空");
        }
        
        // 验证手机号格式
        if (!request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new com.exdemix.backend.exception.BusinessException("手机号格式不正确");
        }
    }
    
    private void setSecurityHeaders(HttpServletResponse response) {
        // 安全相关的HTTP头
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Content-Security-Policy", "default-src 'self'");
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // CORS预检请求处理
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
}
