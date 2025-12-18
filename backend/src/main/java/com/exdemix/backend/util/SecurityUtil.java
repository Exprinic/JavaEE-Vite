// SecurityUtil.java - 安全工具类
package com.exdemix.backend.util;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtil {
    
    /**
     * 获取客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 对于多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * 获取用户代理
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
    /**
     * 验证请求来源
     */
    public static boolean isValidOrigin(HttpServletRequest request, String allowedOrigins) {
        String origin = request.getHeader("Origin");
        if (origin == null) {
            return true; // 没有Origin头，可能是直接访问
        }
        
        String[] origins = allowedOrigins.split(",");
        for (String allowedOrigin : origins) {
            if (origin.trim().equals(allowedOrigin.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 生成CSRF令牌
     */
    public static String generateCsrfToken() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * 验证CSRF令牌
     */
    public static boolean validateCsrfToken(String token, String storedToken) {
        return token != null && token.equals(storedToken);
    }
}