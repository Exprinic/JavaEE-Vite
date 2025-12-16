package com.exdemix.backend.entity.wallpaper;// ================= 3. 壁纸相关实体 =================

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 壁纸实体
 * 包含完整的生命周期管理
 */
@Data
public class Wallpaper {
    private Long id;
    private String title;
    private String description;
    private String originalFilename;     // 原始文件名
    private ImageMetadata metadata;      // 图片元数据封装类
    
    // 资源URL（不同尺寸）
    private String thumbnailUrl;         // 缩略图
    private String mediumUrl;            // 中等尺寸
    private String fullUrl;              // 原图
    private String watermarkUrl;         // 带水印版本
    
    // 业务属性
    private BigDecimal price;
    private WallpaperStatus status;      // DRAFT, PENDING_REVIEW, APPROVED, REJECTED, DELETED
    private DeviceType deviceType;       // 枚举：MOBILE, TABLET, DESKTOP, UNIVERSAL
    private ContentRating rating;        // 内容分级：G, PG, R
    
    // 统计信息
    private Integer viewCount;
    private Integer downloadCount;
    private Integer purchaseCount;
    
    // 关联关系
    private Long uploaderId;             // 上传用户ID
    private Long reviewerId;             // 审核员ID（可为空）
    private LocalDateTime reviewDate;    // 审核日期
    
    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;   // 发布时间
}