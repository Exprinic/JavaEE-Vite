package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.vo.LoginResponseVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService;
    private final Gson gson;

    public LoginServlet() {
        this.authService = new AuthServiceImpl();
        this.gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 从请求中获取参数并封装到DTO对象中
            LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setPhone(request.getParameter("phone"));
            loginRequest.setPassword(request.getParameter("password"));
            loginRequest.setCaptcha(request.getParameter("captcha"));

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

            // 将登录结果写入响应体
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(loginResponse));
        } catch (IOException e) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("isLoggedIn", false);
                session.invalidate();
            }

            // 返回错误响应
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"登录失败\",\"message\":\""+ e.getMessage() +"\"}");
        }
    }
}
