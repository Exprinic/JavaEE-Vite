package com.exdemix.backend.entity.cart;// ================= 5. 购物车系统 =================

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车会话
 * 一个用户对应一个购物车
 */
public class ShoppingCart {
    private Long id;
    private Long userId;
    private String sessionToken;    // 匿名购物车支持
    private CartStatus status;      // ACTIVE, ABANDONED, CONVERTED
    
    // 缓存字段（避免频繁计算）
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private Integer itemCount;
    
    // 时间管理
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt; // 购物车过期时间
    
    // 关联项
    private List<CartItem> items;
    
    // 业务方法
    public void calculateTotals() {
        this.subtotal = items.stream()
            .filter(CartItem::isSelected)
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.itemCount = (int) items.stream()
            .filter(CartItem::isSelected)
            .count();
            
        // 计算折扣和税费（根据业务规则）
        this.total = subtotal.subtract(discount).add(tax);
    }
}
