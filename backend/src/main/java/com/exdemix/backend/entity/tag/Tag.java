package com.exdemix.backend.entity.tag;// ================= 4. 标签系统 =================

import java.time.LocalDateTime;

/**
 * 标签实体
 * 支持分类和筛选
 */
public class Tag {
    private Long id;
    private String name;
    private TagType type;           // CATEGORY, STYLE, COLOR, DEVICE, CUSTOM
    private String slug;            // URL友好标识
    private String description;
    private Integer usageCount;     // 使用次数
    private Long createdBy;         // 创建者ID
    private LocalDateTime createdAt;
    
    // 颜色标签特有
    private String hexColor;
    private Double hue;
    private Double saturation;
    private Double lightness;
}
