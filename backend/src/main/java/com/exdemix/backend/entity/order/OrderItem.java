package com.exdemix.backend.entity.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单项
 * 记录购买的具体商品
 */
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long wallpaperId;
    
    // 购买详情
    private Integer quantity;            // 激活码数量
    private BigDecimal unitPrice;        // 购买时的单价
    private BigDecimal subtotal;
    
    // 商品快照
    private String wallpaperTitle;
    private String wallpaperDescription;
    private String thumbnailUrl;
    
    // 交付信息
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    private LocalDateTime deliveredAt;
    
    // 激活码集合（支付后生成）
    private List<String> activationCodes;
    
    // 业务方法
    public boolean isDelivered() {
        return deliveryStatus == DeliveryStatus.DELIVERED;
    }
}