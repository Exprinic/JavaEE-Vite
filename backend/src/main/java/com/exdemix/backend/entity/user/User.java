package com.exdemix.backend.entity.user;// ================= 1. 用户相关实体 =================

import java.time.LocalDateTime;

/**
 * 用户实体 - 核心业务实体
 * 采用继承或组合实现角色系统
 */
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
    
    // 抽象方法 - 权限检查
    public abstract boolean hasPermission(Permission permission);
}