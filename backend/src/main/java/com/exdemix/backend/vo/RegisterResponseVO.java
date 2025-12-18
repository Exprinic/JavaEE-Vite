package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 注册响应 DTO
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseVO{
    private UserInfoVO userInfo;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    private Set<String> permissions;
    private Set<String> roles;

    private String message;
    private String registerMessage;
    @Builder.Default
    private LocalDateTime registrationTime = LocalDateTime.now();
    private LocalDateTime tokenExpiresAt;
    @Builder.Default
    private Boolean welcomeBonusGranted = false;
    @Builder.Default
    private List<String> onboardingSteps = List.of();
    private String referralCode;
    private Integer initialPoints;
}