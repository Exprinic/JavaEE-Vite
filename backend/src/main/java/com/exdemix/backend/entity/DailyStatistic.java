package com.exdemix.backend.entity;// ================= 11. 统计实体 =================

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日统计
 * 用于报表和分析
 */
public class DailyStatistic {
    private Long id;
    private LocalDate statDate;
    
    // 用户统计
    private Integer newUsers;
    private Integer activeUsers;
    private Integer totalUsers;
    
    // 交易统计
    private Integer ordersCount;
    private BigDecimal ordersRevenue;
    private BigDecimal averageOrderValue;
    
    // 内容统计
    private Integer newWallpapers;
    private Integer wallpaperDownloads;
    private Integer wallpaperPurchases;
    
    // 性能指标
    private BigDecimal conversionRate;   // 转化率
    private BigDecimal refundRate;       // 退款率
}