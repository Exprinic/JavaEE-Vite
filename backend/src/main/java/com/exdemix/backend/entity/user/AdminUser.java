// AdminUser.java
package com.exdemix.backend.entity.user;

public class AdminUser extends RegularUser {

    public AdminUser() {
        super();
        this.userType = UserType.ADMIN;
        this.roles.add(Role.ROLE_ADMIN.name());
        this.roles.add(Role.ROLE_USER.name()); // 管理员也是用户
    }
}

