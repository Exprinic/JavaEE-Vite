package com.exdemix.backend.entity.activation;// ================= 7. 激活码系统 =================

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 激活码实体
 * 独立的价格管理，与购买时价格分离
 */
public class ActivationCode {
    private Long id;
    private String code;                 // 格式：WP-ABC123-XYZ789
    private Long wallpaperId;
    
    // 状态管理
    private ActivationStatus status;     // AVAILABLE, RESERVED, ACTIVATED, EXPIRED, REVOKED
    
    // 定价信息（与购买价分离）
    private BigDecimal faceValue;        // 面值（壁纸定价）
    private BigDecimal sellingPrice;     // 实际售价（可能打折）
    
    // 时间管理
    private LocalDateTime generatedAt;
    private LocalDateTime reservedAt;    // 订单预留时间
    private LocalDateTime activatedAt;
    private LocalDateTime expiresAt;     // 过期时间
    
    // 关联信息
    private Long batchId;                // 批次ID（批量生成）
    private Long orderItemId;            // 关联的订单项
    private Long activatedBy;            // 激活用户ID
    private Long purchasedBy;            // 购买用户ID（可能不同）
    
    // 业务方法
    public boolean isValid() {
        return status == ActivationStatus.AVAILABLE 
            || status == ActivationStatus.RESERVED;
    }
    
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}