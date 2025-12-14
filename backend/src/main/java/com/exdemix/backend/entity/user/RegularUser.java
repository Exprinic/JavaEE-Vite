package com.exdemix.backend.entity.user;

import java.math.BigDecimal;

/**
 * 普通用户
 */
public class RegularUser extends User {
    private BigDecimal balance;          // 账户余额
    private Integer uploadCount;         // 上传数量
    private Integer purchaseCount;       // 购买数量
    
    @Override
    public boolean hasPermission(Permission permission) {
        return permission.getLevel() <= PermissionLevel.USER.getLevel();
    }
}