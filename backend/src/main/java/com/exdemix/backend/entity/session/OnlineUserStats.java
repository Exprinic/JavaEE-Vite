package com.exdemix.backend.entity.session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 在线用户统计实体
 * 用于实时统计和缓存
 */
public class OnlineUserStats {
    private LocalDateTime snapshotTime;     // 快照时间
    private Integer totalOnlineUsers;       // 总在线用户数
    private Map<String, Integer> onlineByRole;  // 按角色统计
    private Map<String, Integer> onlineByDevice; // 按设备统计
    private List<Long> onlineUserIds;       // 在线用户ID列表
    private Long averageSessionDuration;    // 平均会话时长（分钟）
    
    // 统计方法
    public void calculateStats(List<UserSession> activeSessions) {
        this.totalOnlineUsers = activeSessions.size();
        this.snapshotTime = LocalDateTime.now();
        
        // 按角色统计
        this.onlineByRole = activeSessions.stream()
            .collect(Collectors.groupingBy(
                session -> getUserRole(session.getUserId()),
                Collectors.summingInt(e -> 1)
            ));
        
        // 按设备统计
        this.onlineByDevice = activeSessions.stream()
            .filter(s -> s.getDeviceInfo() != null)
            .collect(Collectors.groupingBy(
                UserSession::getDeviceInfo,
                Collectors.summingInt(e -> 1)
            ));
    }

    private String getUserRole(Long userId) {
        return null;
    }
}