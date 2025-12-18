// UserSession.java (新增实体类)
package com.exdemix.backend.entity.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSession {
    private String sessionId;
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private LocalDateTime expiresAt;
    private SessionStatus status;
    
    public enum SessionStatus {
        ACTIVE, EXPIRED, REVOKED, LOGOUT
    }
    
    public boolean isValid() {
        return status == SessionStatus.ACTIVE && expiresAt.isAfter(LocalDateTime.now());
    }
    
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
