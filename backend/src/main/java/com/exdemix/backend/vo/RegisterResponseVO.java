package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class RegisterResponseVO{
    private UserInfoVO userInfo;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    private Set<Permission> permissions;
    private List<String> roles;

    private String message;
    private String registerMessage;
}