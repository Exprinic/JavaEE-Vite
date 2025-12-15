package com.exdemix.backend.entity.stastic;// ================= 9. 支付记录 =================

import com.exdemix.backend.entity.order.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录（与订单解耦）
 * 支持多次支付（如分期、部分支付）
 */
public class PaymentRecord {
    private Long id;
    private Long orderId;
    private String paymentNumber;        // 支付流水号
    
    // 支付信息
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private PaymentStatus status;
    
    // 第三方信息
    private String thirdPartyId;
    private String thirdPartyResponse;   // 原始响应（JSON）
    
    // 时间线
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime refundedAt;
    
    // 安全信息
    private String ipAddress;
    private String userAgent;
}
