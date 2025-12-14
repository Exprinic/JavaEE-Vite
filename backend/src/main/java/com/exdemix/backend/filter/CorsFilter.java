package com.exdemix.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("====== CorsFilter: doFilter executed ======"); // 添加日志

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 设置CORS头
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Accept, Origin");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // 处理预检请求
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("====== CorsFilter: Processing request for " + httpRequest.getMethod() + " " + httpRequest.getRequestURI() + " ======");

        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            System.err.println("====== CorsFilter: An exception was caught in the filter chain! ======");
            t.printStackTrace();
            throw t; // Re-throw the exception after logging
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("====== CorsFilter: init executed ======"); // 添加日志
    }

    @Override
    public void destroy() {
        // 清理操作
    }
}