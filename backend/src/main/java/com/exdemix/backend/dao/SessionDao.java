// SessionDao.java
package com.exdemix.backend.dao;

import com.exdemix.backend.entity.user.UserSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionDao {
    void saveSession(UserSession session);
    Optional<UserSession> findBySessionId(String sessionId);
    Optional<UserSession> findByAccessToken(String accessToken);
    Optional<UserSession> findByRefreshToken(String refreshToken);
    List<UserSession> findActiveSessionsByUserId(Long userId);
    void updateLastActivity(String sessionId, LocalDateTime lastActivity);
    void revokeSession(String sessionId);
    void revokeAllSessions(Long userId);
    void deleteExpiredSessions();
}