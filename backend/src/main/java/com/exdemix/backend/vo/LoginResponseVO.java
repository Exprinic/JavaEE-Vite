package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class LoginResponseVO{
    private UserInfoVO userInfo;

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    private Set<Permission> permissions;
    private List<String> roles;

    private String message;
}
