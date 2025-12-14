package com.exdemix.backend.entity.session;

import java.time.LocalDateTime;

/**
 * 用户会话实体
 * 记录用户登录会话信息
 */
public class UserSession {
    private String sessionId;           // 会话ID（JWT token或session ID）
    private Long userId;                // 用户ID
    private String ipAddress;           // 用户IP
    private String userAgent;           // 用户代理
    private String deviceInfo;          // 设备信息
    private LocalDateTime loginTime;    // 登录时间
    private LocalDateTime lastActivity; // 最后活动时间
    private SessionStatus status;       // 状态：ACTIVE, IDLE, EXPIRED
    private LocalDateTime expireTime;   // 过期时间
    
    // 业务方法
    public boolean isActive() {
        return status == SessionStatus.ACTIVE 
            && expireTime != null 
            && expireTime.isAfter(LocalDateTime.now());
    }
    
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
        // 延长过期时间（例如延长30分钟）
        this.expireTime = LocalDateTime.now().plusMinutes(30);
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }
    public Long getUserId() {
        return userId;
    }
}
