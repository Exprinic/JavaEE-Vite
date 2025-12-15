package com.exdemix.backend.entity.user;

import java.math.BigDecimal;

/**
 * 普通用户
 */
public class RegularUser extends User {
    private BigDecimal balance;          // 账户余额
    private Integer uploadCount;         // 上传数量
    private Integer purchaseCount;       // 购买数量

    public RegularUser() {
        super();
        this.userType = UserType.REGULAR;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        // 基于角色检查权限
        return getRoles().stream()
                .anyMatch(role -> role.getPermissions().contains(permission));
    }
}