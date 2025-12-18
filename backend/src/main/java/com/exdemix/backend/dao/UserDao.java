// UserDao.java - 添加新方法
package com.exdemix.backend.dao;

import com.exdemix.backend.entity.user.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void updateLoginInfo(Long userId, LocalDateTime loginTime, String ipAddress);
    void updateLogoutInfo(Long userId, LocalDateTime logoutTime);
    void lockUserAccount(Long userId, LocalDateTime lockedUntil);
    void unlockUserAccount(Long userId);
    void incrementFailedAttempts(Long userId);
    void resetFailedAttempts(Long userId);
    List<String> getUserRoles(Long userId);
    List<String> getUserPermissions(Long userId);
}