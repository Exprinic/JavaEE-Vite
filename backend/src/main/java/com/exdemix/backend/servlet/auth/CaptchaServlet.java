// CaptchaServlet.java - 完整重构
package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.CaptchaRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.util.SecurityUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.CaptchaResponseVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/api/auth/captcha")
public class CaptchaServlet extends HttpServlet {
    
    private final AuthService authService;
    private final Gson gson;
    
    public CaptchaServlet() {
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
            
            // 2. 解析请求体
            CaptchaRequestDTO captchaRequest = gson.fromJson(request.getReader(), CaptchaRequestDTO.class);
            
            // 3. 验证请求数据
            validateCaptchaRequest(captchaRequest);
            
            // 4. 调用服务层生成验证码
            CaptchaResponseVO captchaResponse = authService.generateCaptcha(captchaRequest, clientIp);
            
            // 5. 返回成功响应
            ApiResponse<CaptchaResponseVO> apiResponse = ApiResponse.success(captchaResponse);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
            
            log.info("Captcha generated for phone: {}", captchaRequest.getPhone());
            
        } catch (com.exdemix.backend.exception.BusinessException e) {
            // 业务异常
            log.warn("Business exception during captcha generation: {}", e.getMessage());
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
            
        } catch (Exception e) {
            // 系统异常
            log.error("System error during captcha generation", e);
            ApiResponse<String> errorResponse = ApiResponse.error(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "验证码生成失败，请稍后重试"
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
    
    private void validateCaptchaRequest(CaptchaRequestDTO request) {
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
            throw new com.exdemix.backend.exception.BusinessException("手机号格式错误");
        }
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
