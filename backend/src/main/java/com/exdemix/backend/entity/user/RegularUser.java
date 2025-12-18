// RegularUser.java
package com.exdemix.backend.entity.user;

import java.math.BigDecimal;

public class RegularUser extends User {
    private BigDecimal balance;

    public RegularUser() {
        this.userType = UserType.REGULAR;
        this.roles.add(Role.ROLE_USER.name());
    }

    @Override
    public boolean hasPermission(String permissionCode) {
        // 检查用户特定权限
        if (permissions.contains(permissionCode)) {
            return true;
        }

        // 检查角色权限
        return getAllPermissions().contains(permissionCode);
    }
}