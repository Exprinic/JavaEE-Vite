package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 登录响应数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseVO{
    private UserInfoVO userInfo;

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn;

    private Set<String> permissions;
    private Set<String> roles;

    private String message;
    @Builder.Default
    private LocalDateTime loginTime = LocalDateTime.now();
    private LocalDateTime tokenExpiresAt;
    @Builder.Default
    private Boolean firstLogin = false;
    @Builder.Default
    private Boolean requiresTwoFactor = false;
    private Set<String> scopes;
    private String sessionId;

    // 安全信息（用于调试）
    private String clientIp;
    private String deviceType;
}
