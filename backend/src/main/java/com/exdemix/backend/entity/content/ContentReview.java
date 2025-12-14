package com.exdemix.backend.entity.content;// ================= 10. 审核系统 =================

import com.exdemix.backend.entity.ReviewDecision;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容审核记录
 */
public class ContentReview {
    private Long id;
    private Long contentId;              // 壁纸ID
    private ContentType contentType;     // WALLPAPER, PROFILE, COMMENT
    private Long reviewerId;
    
    // 审核结果
    private ReviewDecision decision;     // APPROVE, REJECT, PENDING
    private String comment;
    private List<String> rejectionReasons; // 拒绝原因列表
    
    // 审核规则
    private Integer ruleVersion;         // 审核规则版本
    private Boolean automatedReview;     // 是否自动审核
    
    // 时间戳
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
