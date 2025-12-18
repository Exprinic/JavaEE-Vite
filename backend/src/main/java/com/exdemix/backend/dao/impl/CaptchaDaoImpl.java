// CaptchaDaoImpl.java
package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.CaptchaDao;
import com.exdemix.backend.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class CaptchaDaoImpl implements CaptchaDao {
    
    @Override
    public void saveCaptcha(String phone, String captcha, String captchaType, String ipAddress, LocalDateTime expireTime) {
        String sql = "INSERT INTO captchas (phone, captcha_code, captcha_type, ip_address, expire_time) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            stmt.setString(2, captcha);
            stmt.setString(3, captchaType);
            stmt.setString(4, ipAddress);
            stmt.setTimestamp(5, Timestamp.valueOf(expireTime));
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error saving captcha for phone: " + phone, e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Optional<String> getValidCaptcha(String phone, String captchaType) {
        String sql = "SELECT captcha_code FROM captchas WHERE phone = ? AND captcha_type = ? AND used = FALSE AND expire_time > NOW() ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            stmt.setString(2, captchaType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(rs.getString("captcha_code"));
            }
        } catch (SQLException e) {
            log.error("Error getting valid captcha for phone: " + phone, e);
        }
        return Optional.empty();
    }
    
    @Override
    public void markCaptchaAsUsed(String phone, String captcha, String captchaType) {
        String sql = "UPDATE captchas SET used = TRUE WHERE phone = ? AND captcha_code = ? AND captcha_type = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            stmt.setString(2, captcha);
            stmt.setString(3, captchaType);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error marking captcha as used for phone: " + phone, e);
        }
    }
    
    @Override
    public void deleteExpiredCaptchas() {
        String sql = "DELETE FROM captchas WHERE expire_time <= NOW() OR (created_at <= DATE_SUB(NOW(), INTERVAL 1 DAY) AND used = TRUE)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Error deleting expired captchas", e);
        }
    }
    
    @Override
    public int getRecentCaptchaCount(String phone, LocalDateTime since) {
        String sql = "SELECT COUNT(*) FROM captchas WHERE phone = ? AND created_at >= ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            stmt.setTimestamp(2, Timestamp.valueOf(since));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error getting recent captcha count for phone: " + phone, e);
        }
        return 0;
    }
}