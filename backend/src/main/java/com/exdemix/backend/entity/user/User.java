// User.java
package com.exdemix.backend.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class User {
    protected Long id;
    protected String username;
    protected String nickname;
    protected String email;
    protected String phone;
    protected String passwordHash;
    protected String salt;
    protected String avatarUrl;
    protected String bio;
    protected UserStatus status;
    protected LocalDateTime createdAt;
    protected LocalDateTime lastLoginAt;
    protected LocalDateTime lastLogoutAt;
    protected LocalDateTime lockedUntil;
    protected Integer failedLoginAttempts = 0;
    protected String registrationIp;
    protected LocalDateTime registrationTime;
    protected Boolean requireCaptcha = true;
    protected Boolean twoFactorEnabled = false;
    protected UserType userType;
    protected Set<String> permissions = new HashSet<>();
    protected Set<String> roles = new HashSet<>();

    // 抽象方法
    public abstract boolean hasPermission(String permissionCode);

    // 获取所有权限（包括角色权限）
    public Set<String> getAllPermissions() {
        Set<String> allPermissions = new HashSet<>(permissions);

        // 根据角色添加权限
        if (roles.contains(Role.ROLE_USER.name())) {
            allPermissions.addAll(Role.ROLE_USER.getPermissions().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet()));
        }
        if (roles.contains(Role.ROLE_ADMIN.name())) {
            allPermissions.addAll(Role.ROLE_ADMIN.getPermissions().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet()));
        }
        if (roles.contains(Role.ROLE_SUPER_ADMIN.name())) {
            allPermissions.addAll(Role.ROLE_SUPER_ADMIN.getPermissions().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet()));
        }

        return allPermissions;
    }

    // 检查是否被锁定
    public boolean isLocked() {
        if (lockedUntil == null) return false;
        return LocalDateTime.now().isBefore(lockedUntil);
    }

    // 锁定账户
    public void lockAccount(int minutes) {
        this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
        this.status = UserStatus.SUSPENDED;
    }

    // 解锁账户
    public void unlockAccount() {
        this.lockedUntil = null;
        this.status = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
    }

    // 增加登录失败次数
    public void incrementFailedAttempts(int maxAttempts) {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= maxAttempts) {
            lockAccount(30); // 锁定30分钟
        }
    }

    // 重置失败次数
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }
}