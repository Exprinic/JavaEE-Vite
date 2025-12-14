-- 修复后的完整SQL文件（规范化为MySQL支持的格式）

-- 1. 用户表
CREATE TABLE users (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    uuid CHAR(36) NOT NULL UNIQUE COMMENT '全局唯一ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) NULL UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    password_hash CHAR(64) NOT NULL COMMENT '密码哈希',
    avatar_url VARCHAR(500) DEFAULT '/default-avatar.png',
    bio TEXT,

    -- 账户信息
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    account_status ENUM('ACTIVE', 'SUSPENDED', 'BANNED', 'DELETED') DEFAULT 'ACTIVE',
    account_type ENUM('REGULAR', 'CONTENT_CREATOR', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN') DEFAULT 'REGULAR',

    -- 统计信息
    upload_count INT DEFAULT 0,
    purchase_count INT DEFAULT 0,
    total_spent DECIMAL(10,2) DEFAULT 0.00,

    -- 时间戳
    email_verified_at TIMESTAMP NULL,
    phone_verified_at TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 索引
    INDEX idx_account_status (account_status),
    INDEX idx_account_type (account_type),
    INDEX idx_created_at (created_at),
    FULLTEXT INDEX idx_username_nickname (username, nickname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 权限表（RBAC模型）
CREATE TABLE permissions (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限代码',
    name VARCHAR(100) NOT NULL COMMENT '权限名称',
    description TEXT,
    module VARCHAR(50) COMMENT '所属模块',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT='权限表';

CREATE TABLE user_permissions (
    user_id BIGINT UNSIGNED NOT NULL,
    permission_id INT UNSIGNED NOT NULL,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by BIGINT UNSIGNED,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) COMMENT='用户权限关联表';

-- 3. 壁纸表
CREATE TABLE wallpapers (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    uuid CHAR(36) NOT NULL UNIQUE COMMENT '全局唯一ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    description TEXT,

    -- 资源URL
    thumbnail_url VARCHAR(500) NOT NULL,
    medium_url VARCHAR(500) NOT NULL,
    full_url VARCHAR(500) NOT NULL,
    watermark_url VARCHAR(500),

    -- 业务属性
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    status ENUM('DRAFT', 'PENDING_REVIEW', 'APPROVED', 'REJECTED', 'DELETED') DEFAULT 'DRAFT',
    device_type ENUM('MOBILE', 'TABLET', 'DESKTOP', 'UNIVERSAL') DEFAULT 'UNIVERSAL',
    content_rating ENUM('G', 'PG', 'R') DEFAULT 'G',

    -- 统计信息
    view_count INT DEFAULT 0,
    download_count INT DEFAULT 0,
    purchase_count INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,

    -- 关联信息
    uploader_id BIGINT UNSIGNED NOT NULL COMMENT '上传者',
    reviewer_id BIGINT UNSIGNED NULL COMMENT '审核员',
    category_id INT UNSIGNED NULL COMMENT '主分类',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    published_at TIMESTAMP NULL,

    -- 索引
    FOREIGN KEY (uploader_id) REFERENCES users(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    INDEX idx_status (status),
    INDEX idx_uploader_status (uploader_id, status),
    INDEX idx_price (price),
    INDEX idx_published_at (published_at),
    FULLTEXT INDEX idx_title_description (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='壁纸表';

-- 4. 壁纸元数据表
CREATE TABLE wallpaper_metadata (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    wallpaper_id BIGINT UNSIGNED NOT NULL UNIQUE,
    width INT NOT NULL,
    height INT NOT NULL,
    format ENUM('JPEG', 'PNG', 'WEBP', 'GIF', 'BMP') NOT NULL,
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    aspect_ratio DECIMAL(5,2) NOT NULL,
    has_transparency BOOLEAN DEFAULT FALSE,
    color_palette JSON COMMENT '颜色调色板',
    exif_data JSON COMMENT 'EXIF元数据',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (wallpaper_id) REFERENCES wallpapers(id) ON DELETE CASCADE,
    INDEX idx_resolution (width, height),
    INDEX idx_file_size (file_size)
) COMMENT='壁纸元数据表';

-- 5. 标签系统
CREATE TABLE tags (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE COMMENT 'URL友好名称',
    type ENUM('CATEGORY', 'STYLE', 'COLOR', 'DEVICE', 'CUSTOM') DEFAULT 'CUSTOM',
    description TEXT,
    hex_color CHAR(7) NULL COMMENT '颜色标签的十六进制颜色',

    -- 统计
    usage_count INT DEFAULT 0,

    -- 管理信息
    created_by BIGINT UNSIGNED NULL,
    is_system_tag BOOLEAN DEFAULT FALSE COMMENT '是否为系统标签',
    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_type (type),
    INDEX idx_usage_count (usage_count)
) COMMENT='标签表';

CREATE TABLE wallpaper_tags (
    wallpaper_id BIGINT UNSIGNED NOT NULL,
    tag_id INT UNSIGNED NOT NULL,
    added_by BIGINT UNSIGNED NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (wallpaper_id, tag_id),
    FOREIGN KEY (wallpaper_id) REFERENCES wallpapers(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    INDEX idx_tag_id (tag_id)
) COMMENT='壁纸标签关联表';

-- 6. 购物车系统
CREATE TABLE shopping_carts (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NULL COMMENT '用户ID（可为空，支持匿名购物车）',
    session_token VARCHAR(100) NULL COMMENT '匿名购物车会话令牌',

    -- 金额缓存
    subtotal DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    item_count INT DEFAULT 0,

    -- 状态
    status ENUM('ACTIVE', 'ABANDONED', 'CONVERTED', 'EXPIRED') DEFAULT 'ACTIVE',

    -- 时间管理
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL COMMENT '购物车过期时间',
    converted_at TIMESTAMP NULL COMMENT '转化为订单的时间',

    -- 索引
    UNIQUE KEY uk_user_cart (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_status_expires (status, expires_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT='购物车表';

CREATE TABLE cart_items (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT UNSIGNED NOT NULL,
    wallpaper_id BIGINT UNSIGNED NOT NULL,

    -- 购买信息
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL COMMENT '加入时的单价快照',
    current_price DECIMAL(10,2) NOT NULL COMMENT '当前价格',
    currency CHAR(3) DEFAULT 'CNY',

    -- 商品快照
    wallpaper_title VARCHAR(200) NOT NULL,
    thumbnail_url VARCHAR(500),
    author_name VARCHAR(100),

    -- 状态
    is_selected BOOLEAN DEFAULT TRUE,
    is_available BOOLEAN DEFAULT TRUE,
    unavailable_reason VARCHAR(100),

    -- 时间戳
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 约束和索引
    UNIQUE KEY uk_cart_wallpaper (cart_id, wallpaper_id),
    FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE,
    FOREIGN KEY (wallpaper_id) REFERENCES wallpapers(id) ON DELETE CASCADE,
    INDEX idx_cart_id (cart_id)
) COMMENT='购物车项表';

-- 7. 订单系统
CREATE TABLE orders (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT UNSIGNED NOT NULL,

    -- 金额信息
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    currency CHAR(3) DEFAULT 'CNY',
    coupon_code VARCHAR(50) NULL,

    -- 订单状态
    status ENUM('PENDING', 'PROCESSING', 'PAID', 'COMPLETED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    payment_status ENUM('UNPAID', 'PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'UNPAID',

    -- 支付信息
    payment_method ENUM('ALIPAY', 'WECHAT_PAY', 'CREDIT_CARD', 'BALANCE', 'OTHER') NULL,
    payment_transaction_id VARCHAR(100) NULL,
    paid_at TIMESTAMP NULL,

    -- 联系信息
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20),

    -- 时间线
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,

    -- 索引
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_payment_status (payment_status)
) COMMENT='订单表';

CREATE TABLE order_items (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT UNSIGNED NOT NULL,
    wallpaper_id BIGINT UNSIGNED NOT NULL,

    -- 购买详情
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    -- 商品快照
    wallpaper_title VARCHAR(200) NOT NULL,
    wallpaper_description TEXT,
    thumbnail_url VARCHAR(500),

    -- 交付状态
    delivery_status ENUM('PENDING', 'PROCESSING', 'DELIVERED', 'FAILED') DEFAULT 'PENDING',
    delivered_at TIMESTAMP NULL,

    -- 激活码（JSON数组）
    activation_codes JSON NULL,

    -- 索引
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (wallpaper_id) REFERENCES wallpapers(id),
    INDEX idx_order_id (order_id),
    INDEX idx_delivery_status (delivery_status)
) COMMENT='订单项表';

-- 8. 激活码系统
CREATE TABLE activation_codes (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '激活码',
    wallpaper_id BIGINT UNSIGNED NOT NULL,

    -- 状态管理
    status ENUM('AVAILABLE', 'RESERVED', 'ACTIVATED', 'EXPIRED', 'REVOKED') DEFAULT 'AVAILABLE',

    -- 定价信息
    face_value DECIMAL(10,2) NOT NULL COMMENT '面值',
    selling_price DECIMAL(10,2) NOT NULL COMMENT '实际售价',

    -- 时间管理
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reserved_at TIMESTAMP NULL,
    activated_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL COMMENT '过期时间',

    -- 关联信息
    batch_id BIGINT UNSIGNED NULL COMMENT '批次ID',
    order_item_id BIGINT UNSIGNED NULL COMMENT '订单项ID',
    activated_by BIGINT UNSIGNED NULL COMMENT '激活用户',
    purchased_by BIGINT UNSIGNED NULL COMMENT '购买用户',

    -- 索引
    FOREIGN KEY (wallpaper_id) REFERENCES wallpapers(id),
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE SET NULL,
    FOREIGN KEY (activated_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (purchased_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_code (code),
    INDEX idx_status (status),
    INDEX idx_wallpaper_status (wallpaper_id, status),
    INDEX idx_expires_at (expires_at),
    INDEX idx_activated_by (activated_by)
) COMMENT='激活码表';

-- 9. 用户壁纸关系表（统一关系管理）
CREATE TABLE user_wallpaper_relations (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    wallpaper_id BIGINT UNSIGNED NOT NULL,

    -- 关系类型
    relation_type ENUM('UPLOADED', 'PURCHASED', 'ACTIVATED', 'FAVORITED', 'CURRENTLY_USING', 'DOWNLOADED') NOT NULL,

    -- 状态信息
    is_active BOOLEAN DEFAULT FALSE COMMENT '如是否当前使用',
    usage_count INT DEFAULT 0,
    last_used_at TIMESTAMP NULL,

    -- 偏好设置（JSON格式）
    display_settings JSON COMMENT '显示设置',
    user_rating TINYINT CHECK (user_rating BETWEEN 1 AND 5),
    user_review TEXT,

    -- 审核信息（仅对UPLOADED类型有效）
    review_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_by BIGINT UNSIGNED NULL,
    reviewed_at TIMESTAMP NULL,
    review_comment TEXT,

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 约束和索引
    UNIQUE KEY uk_user_wallpaper_type (user_id, wallpaper_id, relation_type),

    -- 移除了不被MySQL支持的带WHERE条件的部分唯一索引
    -- 原语句: UNIQUE KEY uk_user_current_wallpaper (user_id) WHERE relation_type = 'CURRENTLY_USING' AND is_active = TRUE

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (wallpaper_id) REFERENCES wallpapers(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_user_relation (user_id, relation_type),
    INDEX idx_wallpaper_relation (wallpaper_id, relation_type),
    INDEX idx_review_status (review_status)
) COMMENT='用户壁纸关系表';

-- 10. 支付记录表
CREATE TABLE payment_records (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT UNSIGNED NOT NULL,
    payment_number VARCHAR(100) NOT NULL UNIQUE COMMENT '支付流水号',

    -- 支付信息
    amount DECIMAL(10,2) NOT NULL,
    currency CHAR(3) DEFAULT 'CNY',
    payment_method ENUM('ALIPAY', 'WECHAT_PAY', 'CREDIT_CARD', 'BALANCE', 'OTHER') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',

    -- 第三方信息
    third_party_id VARCHAR(100) NULL COMMENT '第三方支付ID',
    third_party_response JSON COMMENT '第三方支付原始响应',

    -- 时间线
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    refunded_at TIMESTAMP NULL,

    -- 安全信息
    ip_address VARCHAR(45) NULL,
    user_agent TEXT,

    -- 索引
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_payment_number (payment_number),
    INDEX idx_status (status),
    INDEX idx_initiated_at (initiated_at)
) COMMENT='支付记录表';

-- 11. 审核记录表
CREATE TABLE content_reviews (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT UNSIGNED NOT NULL,
    content_type ENUM('WALLPAPER', 'USER_PROFILE', 'COMMENT', 'REVIEW') DEFAULT 'WALLPAPER',
    reviewer_id BIGINT UNSIGNED NOT NULL,

    -- 审核结果
    decision ENUM('APPROVE', 'REJECT', 'PENDING', 'REQUEST_CHANGES') NOT NULL,
    comment TEXT,
    rejection_reasons JSON COMMENT '拒绝原因列表',

    -- 审核规则
    rule_version INT DEFAULT 1,
    is_automated BOOLEAN DEFAULT FALSE,

    -- 时间戳
    submitted_at TIMESTAMP NULL COMMENT '内容提交时间',
    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 索引
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    INDEX idx_content (content_id, content_type),
    INDEX idx_reviewer (reviewer_id),
    INDEX idx_decision (decision),
    INDEX idx_reviewed_at (reviewed_at)
) COMMENT='内容审核表';

-- 12. 统计表（物化视图或快照）
CREATE TABLE daily_statistics (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    stat_date DATE NOT NULL UNIQUE,

    -- 用户统计
    new_users INT DEFAULT 0,
    active_users INT DEFAULT 0,
    total_users INT DEFAULT 0,

    -- 交易统计
    orders_count INT DEFAULT 0,
    orders_revenue DECIMAL(15,2) DEFAULT 0.00,
    average_order_value DECIMAL(10,2) DEFAULT 0.00,
    successful_payments INT DEFAULT 0,
    failed_payments INT DEFAULT 0,

    -- 内容统计
    new_wallpapers INT DEFAULT 0,
    approved_wallpapers INT DEFAULT 0,
    wallpaper_downloads INT DEFAULT 0,
    wallpaper_purchases INT DEFAULT 0,
    active_wallpapers INT DEFAULT 0,

    -- 性能指标
    conversion_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '转化率(%)',
    refund_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '退款率(%)',
    average_session_duration INT DEFAULT 0 COMMENT '平均会话时长(秒)',

    -- 计算时间
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_stat_date (stat_date)
) COMMENT='每日统计表';


-- 1. 用户会话表（新增）
CREATE TABLE user_sessions (
                               session_id VARCHAR(100) PRIMARY KEY,
                               user_id BIGINT UNSIGNED NOT NULL,

    -- 连接信息
                               ip_address VARCHAR(45) NOT NULL,
                               user_agent TEXT,
                               device_info VARCHAR(100) COMMENT '设备类型: WEB, MOBILE, TABLET',

    -- 会话状态
                               status ENUM('ACTIVE', 'IDLE', 'EXPIRED', 'LOGGED_OUT') DEFAULT 'ACTIVE',
                               login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               expire_time TIMESTAMP NOT NULL,
                               heartbeat_count INT DEFAULT 0,

    -- 索引
                               INDEX idx_user_id (user_id),
                               INDEX idx_status_expire (status, expire_time),
                               INDEX idx_last_activity (last_activity),
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会话表';

-- 2. 用户活动日志表（新增，可选）
CREATE TABLE user_activity_logs (
                                    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                    user_id BIGINT UNSIGNED NOT NULL,
                                    session_id VARCHAR(100),
                                    activity_type VARCHAR(50) NOT NULL COMMENT 'LOGIN, HEARTBEAT, ACTION, LOGOUT',
                                    activity_data JSON COMMENT '活动详情',
                                    ip_address VARCHAR(45),
                                    user_agent TEXT,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    INDEX idx_user_activity (user_id, created_at),
                                    INDEX idx_session_activity (session_id, created_at),
                                    INDEX idx_activity_type (activity_type, created_at),
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='用户活动日志表';

-- 3. 在线统计快照表（新增）
CREATE TABLE online_stats_snapshots (
                                        id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                        snapshot_time TIMESTAMP NOT NULL,
                                        total_online_users INT NOT NULL,
                                        online_regular_users INT DEFAULT 0,
                                        online_admin_users INT DEFAULT 0,
                                        online_web_users INT DEFAULT 0,
                                        online_mobile_users INT DEFAULT 0,
                                        avg_session_duration_minutes INT DEFAULT 0,

    -- 按小时/天的汇总标记
                                        snapshot_type ENUM('REALTIME', 'HOURLY', 'DAILY') DEFAULT 'REALTIME',

                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                        INDEX idx_snapshot_time (snapshot_time),
                                        INDEX idx_snapshot_type (snapshot_type, snapshot_time)
) COMMENT='在线统计快照表';