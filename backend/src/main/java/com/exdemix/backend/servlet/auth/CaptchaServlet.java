package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.CaptchaRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.CaptchaResponseVO;
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
@WebServlet("/api/auth/captcha")
public class CaptchaServlet extends HttpServlet {

    private final AuthService authService;
    private final Gson gson;

    public CaptchaServlet() {
        this.authService = new AuthServiceImpl();
        // 为Gson注册LocalDateTime类型适配器
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 从请求体中读取JSON数据
            CaptchaRequestDTO captchaRequest = gson.fromJson(request.getReader(), CaptchaRequestDTO.class);

            // 调用服务层生成验证码
            CaptchaResponseVO captchaResponse = authService.generateCaptcha(captchaRequest);

            // 使用统一响应格式
            ApiResponse<CaptchaResponseVO> apiResponse = ApiResponse.success(captchaResponse);

            // 将验证码结果写入响应体
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
        } catch (Exception e) {
            log.error("验证码生成失败", e);

            // 返回错误响应
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage() != null ? e.getMessage() : "验证码生成失败");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}