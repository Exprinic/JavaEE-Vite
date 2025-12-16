package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import com.exdemix.backend.entity.user.UserType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class LoginResponseVO{
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;

    private UserType userType;
    private String accessToken;
    private Set<Permission> permissions;
    private List<String> roles;

    private LocalDateTime loginTime;
    private String message;
}
