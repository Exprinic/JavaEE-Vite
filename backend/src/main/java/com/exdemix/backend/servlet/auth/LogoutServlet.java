package com.exdemix.backend.servlet.auth;

import com.exdemix.backend.dto.LogoutRequestDTO;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.service.impl.AuthServiceImpl;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.vo.LogoutResponseVO;
import com.google.gson.*;
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
@WebServlet("/api/auth/logout")
public class LogoutServlet extends HttpServlet {
    private final AuthService authService;
    private final Gson gson;

    public LogoutServlet() {
        this.authService = new AuthServiceImpl();
        this.gson = GsonUtil.getGson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LogoutRequestDTO logoutRequest = gson.fromJson(req.getReader(), LogoutRequestDTO.class);

            LogoutResponseVO logoutResponseVO = authService.logout(logoutRequest);
            ApiResponse<LogoutResponseVO> apiResponse = ApiResponse.success(logoutResponseVO);

            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(apiResponse));
        } catch (Exception e) {
            log.error("Logout failed", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
