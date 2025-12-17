package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.LoginResponseVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService;
    private final Gson gson;

    public LoginServlet() {
        this.authService = new AuthServiceImpl();
        // 为Gson注册LocalDateTime类型适配器
        this.gson = GsonUtil.getGson();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 从请求体中读取JSON数据
            LoginRequestDTO loginRequest = new Gson().fromJson(request.getReader(), LoginRequestDTO.class);

            // 调用服务层进行登录逻辑处理
            LoginResponseVO loginResponse = authService.login(loginRequest);
            //创建 session
            HttpSession session = request.getSession(true);

            //存储用户信息到session
            session.setAttribute("user", loginResponse.getUserId());
            session.setAttribute("userType", loginResponse.getUserType());
            session.setAttribute("username", loginResponse.getUsername());
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("loginTime", loginResponse.getLoginTime());

            // 设置 Session 过期时间
            session.setMaxInactiveInterval(30 * 60); // 30分钟

            // 使用统一响应格式
            ApiResponse<LoginResponseVO> apiResponse = ApiResponse.success(loginResponse);

            // 将登录结果写入响应体
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
        } catch (Exception e) {
            log.error("登录失败", e); // 打印异常堆栈以便调试
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("isLoggedIn", false);
                session.invalidate();
            }

            // 返回错误响应
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage() != null ? e.getMessage() : "登录失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }

}