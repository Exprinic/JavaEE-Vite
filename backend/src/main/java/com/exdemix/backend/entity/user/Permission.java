package com.exdemix.backend.entity.user;// ================= 2. 权限系统 =================
/**
 * 权限枚举 - 细化权限控制
 */
public enum Permission {
    // 内容权限
    UPLOAD_WALLPAPER(1, "上传壁纸"),
    DELETE_OWN_WALLPAPER(2, "删除自己的壁纸"),
    PURCHASE_WALLPAPER(3, "购买壁纸"),
    
    // 管理权限
    MANAGE_ALL_WALLPAPERS(101, "管理所有壁纸"),
    REVIEW_UPLOADS(102, "审核上传"),
    MANAGE_USERS(103, "管理用户"),
    VIEW_STATISTICS(104, "查看统计"),
    
    // 系统权限
    MANAGE_SYSTEM(201, "系统管理");
    
    private final int code;
    private final String description;

    Permission(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getLevel() {
        return code;
    }
}