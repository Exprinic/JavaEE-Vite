package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.RegisterRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.RegisterResponseVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService;
    private final Gson gson;

    public RegisterServlet() {
        this.authService = new AuthServiceImpl();
        // 为Gson注册LocalDateTime类型适配器
        this.gson = GsonUtil.getGson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 从请求体中读取JSON数据
            RegisterRequestDTO registerRequest = gson.fromJson(request.getReader(), RegisterRequestDTO.class);

            // 调用服务层进行注册逻辑处理
            RegisterResponseVO registerResponse = authService.register(registerRequest);

            // 使用统一响应格式
            ApiResponse<RegisterResponseVO> apiResponse = ApiResponse.success(registerResponse);

            // 将注册结果写入响应体
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
        } catch (Exception e) {
            log.error("注册失败", e);

            // 返回错误响应
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage() != null ? e.getMessage() : "注册失败");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}