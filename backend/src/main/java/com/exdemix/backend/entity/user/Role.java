package com.exdemix.backend.entity.user;

import java.util.Arrays;
import java.util.List;

public enum Role {
    ROLE_USER(
        "普通用户",
        Arrays.asList(
            Permission.UPLOAD_WALLPAPER,
            Permission.DELETE_OWN_WALLPAPER,
            Permission.PURCHASE_WALLPAPER
        )
    ),

    ROLE_ADMIN(
        "管理员",
        Arrays.asList(
            Permission.MANAGE_ALL_WALLPAPERS,
            Permission.REVIEW_UPLOADS,
            Permission.MANAGE_USERS,
            Permission.VIEW_STATISTICS
        )
    ),

    ROLE_SUPER_ADMIN(
        "超级管理员",
        Arrays.asList(
            Permission.MANAGE_SYSTEM,
            Permission.MANAGE_ALL_WALLPAPERS,
            Permission.REVIEW_UPLOADS,
            Permission.MANAGE_USERS,
            Permission.VIEW_STATISTICS
        )
    );

    private final String displayName;
    private final List<Permission> permissions;

    Role(String displayName, List<Permission> permissions) {
        this.displayName = displayName;
        this.permissions = permissions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}

