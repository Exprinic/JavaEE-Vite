package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import com.exdemix.backend.entity.user.UserStatus;
import com.exdemix.backend.entity.user.UserType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class RegisterResponseVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String bio;
    private UserStatus status;
    private UserType userType;
    private Set<Permission> permissions;
    private String message;
}
