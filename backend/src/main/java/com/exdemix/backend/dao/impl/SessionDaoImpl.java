// SessionDaoImpl.java
package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.SessionDao;
import com.exdemix.backend.entity.user.UserSession;
import com.exdemix.backend.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SessionDaoImpl implements SessionDao {
    
    @Override
    public void saveSession(UserSession session) {
        String sql = "INSERT INTO user_sessions (session_id, user_id, access_token, refresh_token, device_info, "
                   + "ip_address, user_agent, login_time, last_activity, expires_at, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE last_activity = VALUES(last_activity), status = VALUES(status)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, session.getSessionId());
            stmt.setLong(2, session.getUserId());
            stmt.setString(3, session.getAccessToken());
            stmt.setString(4, session.getRefreshToken());
            stmt.setString(5, session.getDeviceInfo());
            stmt.setString(6, session.getIpAddress());
            stmt.setString(7, session.getUserAgent());
            stmt.setTimestamp(8, Timestamp.valueOf(session.getLoginTime()));
            stmt.setTimestamp(9, Timestamp.valueOf(session.getLastActivity()));
            stmt.setTimestamp(10, Timestamp.valueOf(session.getExpiresAt()));
            stmt.setString(11, session.getStatus().name());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error saving session for user id: " + session.getUserId(), e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Optional<UserSession> findBySessionId(String sessionId) {
        String sql = "SELECT * FROM user_sessions WHERE session_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding session by session id: " + sessionId, e);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<UserSession> findByAccessToken(String accessToken) {
        String sql = "SELECT * FROM user_sessions WHERE access_token = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accessToken);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding session by access token", e);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<UserSession> findByRefreshToken(String refreshToken) {
        String sql = "SELECT * FROM user_sessions WHERE refresh_token = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, refreshToken);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding session by refresh token", e);
        }
        return Optional.empty();
    }
    
    @Override
    public List<UserSession> findActiveSessionsByUserId(Long userId) {
        List<UserSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM user_sessions WHERE user_id = ? AND status = 'ACTIVE' AND expires_at > NOW()";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding active sessions for user id: " + userId, e);
        }
        return sessions;
    }
    
    @Override
    public void updateLastActivity(String sessionId, LocalDateTime lastActivity) {
        String sql = "UPDATE user_sessions SET last_activity = ? WHERE session_id = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(lastActivity));
            stmt.setString(2, sessionId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error updating last activity for session id: " + sessionId, e);
        }
    }
    
    @Override
    public void revokeSession(String sessionId) {
        String sql = "UPDATE user_sessions SET status = 'REVOKED' WHERE session_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sessionId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error revoking session: " + sessionId, e);
        }
    }
    
    @Override
    public void revokeAllSessions(Long userId) {
        String sql = "UPDATE user_sessions SET status = 'REVOKED' WHERE user_id = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error revoking all sessions for user id: " + userId, e);
        }
    }
    
    @Override
    public void deleteExpiredSessions() {
        String sql = "DELETE FROM user_sessions WHERE expires_at <= NOW() OR status IN ('EXPIRED', 'REVOKED', 'LOGOUT')";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error deleting expired sessions", e);
        }
    }
    
    private UserSession mapResultSetToSession(ResultSet rs) throws SQLException {
        UserSession session = new UserSession();
        
        session.setSessionId(rs.getString("session_id"));
        session.setUserId(rs.getLong("user_id"));
        session.setAccessToken(rs.getString("access_token"));
        session.setRefreshToken(rs.getString("refresh_token"));
        session.setDeviceInfo(rs.getString("device_info"));
        session.setIpAddress(rs.getString("ip_address"));
        session.setUserAgent(rs.getString("user_agent"));
        
        // 时间字段
        Timestamp loginTime = rs.getTimestamp("login_time");
        if (loginTime != null) {
            session.setLoginTime(loginTime.toLocalDateTime());
        }
        
        Timestamp lastActivity = rs.getTimestamp("last_activity");
        if (lastActivity != null) {
            session.setLastActivity(lastActivity.toLocalDateTime());
        }
        
        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null) {
            session.setExpiresAt(expiresAt.toLocalDateTime());
        }
        
        // 状态
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            session.setStatus(UserSession.SessionStatus.valueOf(statusStr));
        }
        
        return session;
    }
}