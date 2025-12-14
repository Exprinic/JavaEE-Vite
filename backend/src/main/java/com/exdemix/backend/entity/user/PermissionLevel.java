package com.exdemix.backend.entity.user;

public enum PermissionLevel {
    GUEST(0),
    USER(1),
    MODERATOR(2),
    ADMIN(3);

    private final int level;

    PermissionLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}