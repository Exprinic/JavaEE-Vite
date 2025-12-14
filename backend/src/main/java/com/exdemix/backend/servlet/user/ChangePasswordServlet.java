package com.exdemix.backend.servlet.user;

import com.exdemix.backend.service.UserService;
import com.exdemix.backend.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/user/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserService userService;

    public ChangePasswordServlet() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        userService.changePassword();
    }
}