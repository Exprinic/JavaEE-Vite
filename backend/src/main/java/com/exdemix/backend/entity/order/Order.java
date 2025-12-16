package com.exdemix.backend.entity.order;// ================= 6. 订单系统 =================

import com.exdemix.backend.entity.stastic.PaymentRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单主表
 * 完整的订单生命周期管理
 */
public class Order {
    private Long id;
    private String orderNumber;          // 订单号：WP202401010001
    private Long userId;
    
    // 金额信息
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private String couponCode;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee = BigDecimal.ZERO; // 虚拟商品通常为0
    private BigDecimal totalAmount;
    private String currency;
    
    // 订单状态（状态机）
    private OrderStatus status;          // PENDING, PROCESSING, PAID, COMPLETED, CANCELLED, REFUNDED
    private PaymentStatus paymentStatus; // UNPAID, PENDING, PAID, FAILED, REFUNDED
    
    // 支付信息
    private String paymentMethod;        // ALIPAY, WECHAT, CREDIT_CARD
    private String paymentTransactionId; // 第三方支付ID
    private LocalDateTime paidAt;
    
    // 配送信息（虽然是虚拟商品，但可能有邮件通知）
    private String email;
    private String contactPhone;
    
    // 时间线
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
    // 关联项
    private List<OrderItem> items;
    private List<PaymentRecord> paymentRecords;
    
    // 业务方法
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING 
            || status == OrderStatus.PROCESSING;
    }
    
    public boolean canRequestRefund() {
        return status == OrderStatus.COMPLETED 
            && paidAt != null 
            && paidAt.isAfter(LocalDateTime.now().minusDays(7)); // 7天内可退款
    }
}