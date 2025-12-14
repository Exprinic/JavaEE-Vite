package com.exdemix.backend.servlet.error;

import com.exdemix.backend.service.ErrorService;
import com.exdemix.backend.service.impl.ErrorServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/error/report")
public class ReportErrorServlet extends HttpServlet {

    private final ErrorService errorService;

    public ReportErrorServlet() {
        this.errorService = new ErrorServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        errorService.report();
    }
}