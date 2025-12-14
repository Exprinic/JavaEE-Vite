package com.exdemix.backend.entity.user;

import java.util.Set;

/**
 * 管理员用户
 */
public class AdminUser extends User {
    private AdminRole role;              // ADMIN, SUPER_ADMIN, CONTENT_MODERATOR
    private Set<Permission> permissions; // 权限集合

    @Override
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}