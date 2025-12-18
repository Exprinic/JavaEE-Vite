// RegisterServlet.java - 完整重构
package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.RegisterRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.util.SecurityUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.RegisterResponseVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {
    
    private final AuthService authService;
    private final Gson gson;
    
    public RegisterServlet() {
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
            RegisterRequestDTO registerRequest = gson.fromJson(request.getReader(), RegisterRequestDTO.class);
            
            // 3. 验证请求数据
            validateRegisterRequest(registerRequest);
            
            // 4. 调用服务层注册
            RegisterResponseVO registerResponse = authService.register(registerRequest, clientIp, userAgent);
            
            // 5. 设置响应头
            setSecurityHeaders(response);
            
            // 6. 返回成功响应
            ApiResponse<RegisterResponseVO> apiResponse = ApiResponse.success(registerResponse);
            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            response.getWriter().write(gson.toJson(apiResponse));
            
            log.info("Registration successful for phone: {}", registerRequest.getPhone());
            
        } catch (com.exdemix.backend.exception.BusinessException e) {
            // 业务异常
            log.warn("Business exception during registration: {}", e.getMessage());
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
            
        } catch (Exception e) {
            // 系统异常
            log.error("System error during registration", e);
            ApiResponse<String> errorResponse = ApiResponse.error(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "系统内部错误，请稍后重试"
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
    
    private void validateRegisterRequest(RegisterRequestDTO request) {
        if (request == null) {
            throw new com.exdemix.backend.exception.BusinessException("请求数据不能为空");
        }
        
        // 验证必填字段
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new com.exdemix.backend.exception.BusinessException("用户名不能为空");
        }
        
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new com.exdemix.backend.exception.BusinessException("手机号不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new com.exdemix.backend.exception.BusinessException("密码不能为空");
        }
        
        if (request.getCaptcha() == null || request.getCaptcha().trim().isEmpty()) {
            throw new com.exdemix.backend.exception.BusinessException("验证码不能为空");
        }
        
        // 验证用户名格式
        if (!request.getNickname().matches("^[a-zA-Z0-9_-]{4,16}$")) {
            throw new com.exdemix.backend.exception.BusinessException("用户名格式错误，只能包含字母、数字、下划线和短横线，长度为4-16个字符");
        }
        
        // 验证手机号格式
        if (!request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new com.exdemix.backend.exception.BusinessException("手机号格式错误");
        }
        
        // 验证密码强度（使用PasswordUtil）
        com.exdemix.backend.util.PasswordUtil passwordUtil = new com.exdemix.backend.util.PasswordUtil();
        if (!passwordUtil.isPasswordStrong(request.getPassword())) {
            throw new com.exdemix.backend.exception.BusinessException("密码强度不足，必须包含大写字母、小写字母、数字和特殊字符中的至少三种，且长度至少8位");
        }
        
        // 验证验证码格式
        if (!request.getCaptcha().matches("\\d{6}")) {
            throw new com.exdemix.backend.exception.BusinessException("验证码必须是6位数字");
        }
    }
    
    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
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
}