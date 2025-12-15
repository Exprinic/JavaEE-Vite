package com.exdemix.backend.entity.preference;// ================= 8. 用户壁纸关系 =================

import com.exdemix.backend.entity.stastic.ReviewStatus;

import javax.management.relation.RelationType;
import java.time.LocalDateTime;

/**
 * 用户与壁纸的多维关系
 * 取代"我的上传"和"用户偏好"两个表
 */
public class UserWallpaperRelation {
    private Long id;
    private Long userId;
    private Long wallpaperId;
    
    // 关系类型（枚举）
    private RelationType type;           // UPLOADED, PURCHASED, ACTIVATED, FAVORITED, CURRENTLY_USING
    
    // 状态信息
    private Boolean isActive;            // 是否活跃（如当前使用的壁纸）
    private Integer usageCount;          // 使用次数
    private LocalDateTime lastUsedAt;    // 最后使用时间
    
    // 偏好设置
    private String displaySettings;      // JSON格式的显示设置
    private Integer rating;              // 用户评分（1-5）
    private String review;               // 用户评价
    
    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 如果是上传关系，包含审核信息
    private ReviewStatus reviewStatus;   // PENDING, APPROVED, REJECTED
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewComment;
}