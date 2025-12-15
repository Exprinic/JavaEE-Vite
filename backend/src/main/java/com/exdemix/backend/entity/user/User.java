package com.exdemix.backend.entity.user;// ================= 1. 用户相关实体 =================

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户实体 - 核心业务实体
 * 采用继承或组合实现角色系统
 */
@Setter
@Getter
public abstract class User {
    protected Long id;
    protected String username;          // 用户名（唯一）
    protected String nickname;          // 昵称
    protected String email;             // 邮箱（唯一）
    protected String phone;             // 手机号（唯一）
    protected String passwordHash;      // 密码哈希
    protected String avatarUrl;         // 头像URL
    protected String bio;               // 自我介绍
    protected UserStatus status;        // 状态：ACTIVE, SUSPENDED, DELETED
    protected LocalDateTime createdAt;
    protected LocalDateTime lastLoginAt;
    protected UserType userType;            // 用户类型：ADMIN, USER
    protected Set<Permission> permissions; // 权限集合
    
    // 抽象方法 - 权限检查
    protected abstract boolean hasPermission(Permission permission);

    // 获取用户角色
    public List<Role> getRoles() {
        return switch (userType) {
            case REGULAR -> List.of(Role.ROLE_USER);
            case ADMIN -> List.of(Role.ROLE_ADMIN);
            default -> List.of();
        };
    }
}