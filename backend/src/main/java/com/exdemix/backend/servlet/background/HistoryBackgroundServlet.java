package com.exdemix.backend.servlet.background;

import com.exdemix.backend.service.BackgroundService;
import com.exdemix.backend.service.impl.BackgroundServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/background/history")
public class HistoryBackgroundServlet extends HttpServlet {

    private final BackgroundService backgroundService;

    public HistoryBackgroundServlet() {
        this.backgroundService = new BackgroundServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        backgroundService.getBackgroundHistory();
    }
}