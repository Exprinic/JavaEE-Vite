package com.exdemix.backend.entity.cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项
 * 支持激活码数量购买
 */
public class CartItem {
    private Long id;
    private Long cartId;
    private Long wallpaperId;
    
    // 购买信息
    private Integer quantity;            // 激活码数量
    private BigDecimal unitPrice;        // 加入时的单价快照
    private BigDecimal currentPrice;     // 当前价格（实时）
    private String currency;             // 货币类型
    
    // 壁纸信息快照（防商品信息变更）
    private String wallpaperTitle;
    private String thumbnailUrl;
    private String authorName;
    
    // 状态
    private Boolean selected = true;
    private Boolean available = true;
    private String unavailableReason;    // 不可用原因
    
    // 时间戳
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
    
    // 计算属性
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    public boolean isSelected() {
        return selected;
    }
}