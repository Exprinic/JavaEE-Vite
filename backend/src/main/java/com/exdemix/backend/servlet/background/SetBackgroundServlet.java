package com.exdemix.backend.servlet.background;

import com.exdemix.backend.service.BackgroundService;
import com.exdemix.backend.service.impl.BackgroundServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/background/set")
public class SetBackgroundServlet extends HttpServlet {

    private final BackgroundService backgroundService;

    public SetBackgroundServlet() {
        this.backgroundService = new BackgroundServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        backgroundService.setBackground(1);
    }
}