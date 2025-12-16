package com.exdemix.backend.servlet.background;

import com.exdemix.backend.service.BackgroundService;
import com.exdemix.backend.service.impl.BackgroundServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/background/default")
public class DefaultServlet extends HttpServlet {

    private final BackgroundService backgroundService;
    private final Gson gson;

    public DefaultServlet() {
        this.backgroundService = new BackgroundServiceImpl();
        this.gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DefaultServlet.doPost");
        backgroundService.getDefaultBackground();
    }
}