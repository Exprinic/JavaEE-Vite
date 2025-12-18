-- ==================== 1. 用户表重构 ====================
ALTER TABLE users
-- 修改 account_type 枚举值以匹配 UserType
    MODIFY COLUMN account_type ENUM('REGULAR', 'ADMIN', 'SUPER_ADMIN') DEFAULT 'REGULAR',

-- 移除不必要的字段
    DROP COLUMN upload_count,
    DROP COLUMN purchase_count,
    DROP COLUMN total_spent,
    DROP COLUMN uuid,

-- 添加新字段
    ADD COLUMN failed_login_attempts INT DEFAULT 0 COMMENT '登录失败次数',
    ADD COLUMN locked_until TIMESTAMP NULL COMMENT '锁定直到时间',
    ADD COLUMN registration_ip VARCHAR(45) COMMENT '注册IP',
    ADD COLUMN registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    ADD COLUMN salt VARCHAR(64) NOT NULL DEFAULT '' COMMENT '密码盐值',
    ADD COLUMN require_captcha BOOLEAN DEFAULT TRUE COMMENT '是否需要验证码',
    ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE COMMENT '是否启用双因素认证',

-- 修改现有字段
    MODIFY COLUMN password_hash VARCHAR(128) NOT NULL COMMENT '密码哈希（BCrypt）',
    MODIFY COLUMN username VARCHAR(50) NULL UNIQUE COMMENT '用户名（可为空）',

-- 索引优化
    ADD INDEX idx_user_phone_status (phone, account_status),
    ADD INDEX idx_user_email_status (email, account_status),
    ADD INDEX idx_user_status_type (account_status, account_type);

-- ==================== 2. 用户会话表（新增） ====================
CREATE TABLE IF NOT EXISTS user_sessions (
                                             session_id VARCHAR(64) PRIMARY KEY COMMENT '会话ID',
                                             user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                                             access_token VARCHAR(512) NOT NULL COMMENT '访问令牌',
                                             refresh_token VARCHAR(512) NOT NULL COMMENT '刷新令牌',
                                             device_info JSON COMMENT '设备信息',
                                             ip_address VARCHAR(45) NOT NULL COMMENT 'IP地址',
                                             user_agent TEXT COMMENT '用户代理',
                                             login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
                                             last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后活动时间',
                                             expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
                                             status ENUM('ACTIVE', 'EXPIRED', 'REVOKED', 'LOGOUT') DEFAULT 'ACTIVE' COMMENT '会话状态',

    -- 索引
                                             INDEX idx_user_id (user_id),
                                             INDEX idx_access_token (access_token(255)),
                                             INDEX idx_refresh_token (refresh_token(255)),
                                             INDEX idx_status_expires (status, expires_at),
                                             INDEX idx_user_status (user_id, status),

                                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会话表';

-- ==================== 3. 验证码表（新增） ====================
CREATE TABLE IF NOT EXISTS captchas (
                                        id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                        phone VARCHAR(20) NOT NULL COMMENT '手机号',
                                        captcha_code VARCHAR(10) NOT NULL COMMENT '验证码',
                                        captcha_type ENUM('REGISTER', 'LOGIN', 'RESET_PASSWORD') DEFAULT 'LOGIN' COMMENT '验证码类型',
                                        ip_address VARCHAR(45) COMMENT '请求IP',
                                        expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
                                        used BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                        INDEX idx_phone_type (phone, captcha_type),
                                        INDEX idx_expire_time (expire_time),
                                        INDEX idx_used (used)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- ==================== 4. 登录日志表（新增） ====================
CREATE TABLE IF NOT EXISTS login_logs (
                                          id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                          user_id BIGINT UNSIGNED NULL COMMENT '用户ID（可能为空）',
                                          phone VARCHAR(20) COMMENT '登录手机号',
                                          login_type ENUM('PASSWORD', 'CAPTCHA', 'SOCIAL') DEFAULT 'PASSWORD' COMMENT '登录方式',
                                          success BOOLEAN DEFAULT FALSE COMMENT '是否成功',
                                          ip_address VARCHAR(45) COMMENT '登录IP',
                                          user_agent TEXT COMMENT '用户代理',
                                          error_message VARCHAR(255) COMMENT '错误信息',
                                          session_id VARCHAR(64) COMMENT '会话ID',
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                          INDEX idx_user_phone (user_id, phone),
                                          INDEX idx_created_success (created_at, success),
                                          INDEX idx_ip_address (ip_address),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ==================== 5. 角色权限关联表（新增） ====================
CREATE TABLE IF NOT EXISTS user_roles (
                                          id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                          user_id BIGINT UNSIGNED NOT NULL,
                                          role VARCHAR(50) NOT NULL COMMENT '角色：ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN',
                                          assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                          UNIQUE KEY uk_user_role (user_id, role),
                                          INDEX idx_user_id (user_id),
                                          INDEX idx_role (role),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ==================== 6. 用户权限表（新增） ====================
CREATE TABLE IF NOT EXISTS user_permissions (
                                                id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                                user_id BIGINT UNSIGNED NOT NULL,
                                                permission_code VARCHAR(50) NOT NULL COMMENT '权限代码',
                                                permission_name VARCHAR(100) COMMENT '权限名称',
                                                granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                                UNIQUE KEY uk_user_permission (user_id, permission_code),
                                                INDEX idx_user_id (user_id),
                                                INDEX idx_permission_code (permission_code),
                                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限表';

-- ==================== 7. 初始化角色数据 ====================
INSERT INTO user_roles (user_id, role)
SELECT
    u.id,
    CASE
        WHEN u.account_type = 'REGULAR' THEN 'ROLE_USER'
        WHEN u.account_type = 'ADMIN' THEN 'ROLE_ADMIN'
        WHEN u.account_type = 'SUPER_ADMIN' THEN 'ROLE_SUPER_ADMIN'
        ELSE 'ROLE_USER'
        END as role
FROM users u
ON DUPLICATE KEY UPDATE role = VALUES(role);

-- ==================== 8. 初始化权限数据 ====================
-- 为普通用户添加基础权限
INSERT INTO user_permissions (user_id, permission_code, permission_name)
SELECT
    ur.user_id,
    p.code,
    p.description
FROM user_roles ur
         CROSS JOIN (
    -- 普通用户权限
    SELECT 'UPLOAD_WALLPAPER' as code, '上传壁纸' as description UNION ALL
    SELECT 'DELETE_OWN_WALLPAPER', '删除自己的壁纸' UNION ALL
    SELECT 'PURCHASE_WALLPAPER', '购买壁纸'
) p
WHERE ur.role = 'ROLE_USER'
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name);

-- 为管理员添加权限
INSERT INTO user_permissions (user_id, permission_code, permission_name)
SELECT
    ur.user_id,
    p.code,
    p.description
FROM user_roles ur
         CROSS JOIN (
    -- 管理员权限
    SELECT 'MANAGE_ALL_WALLPAPERS' as code, '管理所有壁纸' as description UNION ALL
    SELECT 'REVIEW_UPLOADS', '审核上传' UNION ALL
    SELECT 'MANAGE_USERS', '管理用户' UNION ALL
    SELECT 'VIEW_STATISTICS', '查看统计'
) p
WHERE ur.role = 'ROLE_ADMIN'
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name);

-- 为超级管理员添加权限
INSERT INTO user_permissions (user_id, permission_code, permission_name)
SELECT
    ur.user_id,
    p.code,
    p.description
FROM user_roles ur
         CROSS JOIN (
    -- 超级管理员权限
    SELECT 'MANAGE_SYSTEM' as code, '系统管理' as description UNION ALL
    SELECT 'MANAGE_ALL_WALLPAPERS', '管理所有壁纸' UNION ALL
    SELECT 'REVIEW_UPLOADS', '审核上传' UNION ALL
    SELECT 'MANAGE_USERS', '管理用户' UNION ALL
    SELECT 'VIEW_STATISTICS', '查看统计'
) p
WHERE ur.role = 'ROLE_SUPER_ADMIN'
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name);