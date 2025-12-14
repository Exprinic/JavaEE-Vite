package com.exdemix.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(filterName = "WebConfig", urlPatterns = "/api/*")
public class WebConfig implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("WebConfig: init executed");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("WebConfig: doFilter executed for URI: " + req.getRequestURI());

        // 在这里可以添加您的过滤逻辑，例如：
        // 1. 身份验证：检查 session 或 token
        // 2. 授权：检查用户是否有权限访问该 API
        // 3. 日志记录：记录请求详情

        // 如果检查通过，则继续执行过滤器链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("WebConfig: destroy executed");
    }
}