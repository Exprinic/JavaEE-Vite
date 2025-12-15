package com.exdemix.backend.entity.user;

/**
 * 管理员用户 - 单例模式实现
 */
public class AdminUser extends User {
    // 静态实例
    private static final AdminUser instance = new AdminUser();

    // 私有构造函数
    private AdminUser() {
        super();
        this.userType = UserType.ADMIN;
    }

    // 提供全局访问点
    public static AdminUser getInstance() {
        return instance;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        // 管理员拥有更高权限
        return getRoles().stream()
                .anyMatch(role -> role.getPermissions().contains(permission));
    }
}
