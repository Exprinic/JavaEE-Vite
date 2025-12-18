// SuperAdminUser.java
package com.exdemix.backend.entity.user;

public class SuperAdminUser extends AdminUser {
    
    public SuperAdminUser() {
        super();
        this.userType = UserType.SUPER_ADMIN;
        this.roles.add(Role.ROLE_SUPER_ADMIN.name());
        this.roles.add(Role.ROLE_ADMIN.name());
        this.roles.add(Role.ROLE_USER.name());
    }
}