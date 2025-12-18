// AuthServiceImpl.java - 完整重构
package com.exdemix.backend.service.impl;

import com.exdemix.backend.converter.AuthConverter;
import com.exdemix.backend.dao.CaptchaDao;
import com.exdemix.backend.dao.SessionDao;
import com.exdemix.backend.dao.UserDao;
import com.exdemix.backend.dao.impl.*;
import com.exdemix.backend.dto.*;
import com.exdemix.backend.entity.user.*;
import com.exdemix.backend.exception.BusinessException;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.util.*;
import com.exdemix.backend.vo.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserDao userDao;
    private final CaptchaDao captchaDao;
    private final SessionDao sessionDao;
    private final AuthConverter authConverter;
    private final PasswordUtil passwordUtil;
    private final TokenUtil tokenUtil;
    private final CaptchaUtil captchaUtil;
    
    // 配置常量
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int CAPTCHA_EXPIRE_MINUTES = 10;
    private static final int SESSION_EXPIRE_HOURS = 24;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 30;
    
    public AuthServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.captchaDao = new CaptchaDaoImpl();
        this.sessionDao = new SessionDaoImpl();
        this.authConverter = new AuthConverter();
        this.passwordUtil = new PasswordUtil();
        this.tokenUtil = new TokenUtil();
        this.captchaUtil = new CaptchaUtil();
    }
    
    @Override
    public LoginResponseVO login(LoginRequestDTO loginRequest, String ipAddress, String userAgent) throws InterruptedException {
        log.info("Login attempt - Phone: {}, IP: {}", loginRequest.getPhone(), ipAddress);
        
        // 1. 验证验证码（如果需要）
        User user = null;
        Optional<User> userOptional = userDao.findByPhone(loginRequest.getPhone());
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getRequireCaptcha()) {
                if (!validateCaptcha(loginRequest.getPhone(), loginRequest.getCaptcha(), "LOGIN")) {
                    log.warn("Invalid captcha for login - Phone: {}", loginRequest.getPhone());
                    throw new BusinessException("验证码错误或已过期");
                }
            }
        }
        
        // 2. 检查账户锁定
        if (user != null && user.isLocked()) {
            log.warn("Account is locked - User ID: {}, Phone: {}", user.getId(), loginRequest.getPhone());
            throw new BusinessException("账户已被锁定，请" + user.getLockedUntil() + "后再试");
        }
        
        // 3. 验证用户存在性
        if (user == null) {
            log.warn("User not found - Phone: {}", loginRequest.getPhone());
            // 增加登录失败计数（防止用户枚举攻击）
            Thread.sleep(1000); // 防止时序攻击
            throw new BusinessException("手机号或密码错误");
        }
        
        // 4. 验证密码
        if (!passwordUtil.verifyPassword(loginRequest.getPassword(), user.getPasswordHash(), user.getSalt())) {
            log.warn("Invalid password - User ID: {}, Phone: {}", user.getId(), loginRequest.getPhone());
            
            // 增加失败次数
            userDao.incrementFailedAttempts(user.getId());
            user.incrementFailedAttempts(MAX_LOGIN_ATTEMPTS);
            
            if (user.isLocked()) {
                userDao.lockUserAccount(user.getId(), user.getLockedUntil());
                log.warn("Account locked due to too many failed attempts - User ID: {}", user.getId());
                throw new BusinessException("密码错误次数过多，账户已锁定30分钟");
            }
            
            throw new BusinessException("手机号或密码错误");
        }
        
        // 5. 检查账户状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Account is not active - User ID: {}, Status: {}", user.getId(), user.getStatus());
            throw new BusinessException("账户状态异常，请联系管理员");
        }
        
        // 6. 重置失败次数
        userDao.resetFailedAttempts(user.getId());
        user.resetFailedAttempts();
        
        // 7. 生成令牌
        String accessToken = tokenUtil.generateAccessToken(user);
        String refreshToken = tokenUtil.generateRefreshToken(user);
        
        // 8. 创建会话
        UserSession session = createUserSession(user, accessToken, refreshToken, ipAddress, userAgent);
        sessionDao.saveSession(session);
        
        // 9. 更新登录信息
        userDao.updateLoginInfo(user.getId(), LocalDateTime.now(), ipAddress);
        user.setLastLoginAt(LocalDateTime.now());
        
        // 10. 标记验证码为已使用
        if (user.getRequireCaptcha()) {
            captchaDao.markCaptchaAsUsed(loginRequest.getPhone(), loginRequest.getCaptcha(), "LOGIN");
        }
        
        // 11. 记录登录日志
        logLogin(user.getId(), loginRequest.getPhone(), true, ipAddress, userAgent, "密码登录");
        
        log.info("Login successful - User ID: {}, Phone: {}", user.getId(), loginRequest.getPhone());
        
        // 12. 返回登录响应
        return authConverter.toLoginVO(
            user, 
            accessToken, 
            refreshToken, 
            "登录成功",
            session.getExpiresAt()
        );
    }
    
    @Override
    public RegisterResponseVO register(RegisterRequestDTO registerRequest, String ipAddress, String userAgent) {
        log.info("Registration attempt - Phone: {}, Nickname: {}, IP: {}", 
                registerRequest.getPhone(), registerRequest.getNickname(), ipAddress);
        
        // 1. 验证验证码
        if (!validateCaptcha(registerRequest.getPhone(), registerRequest.getCaptcha(), "REGISTER")) {
            log.warn("Invalid captcha for registration - Phone: {}", registerRequest.getPhone());
            throw new BusinessException("验证码错误或已过期");
        }
        
        // 2. 检查手机号是否已注册
        if (userDao.existsByPhone(registerRequest.getPhone())) {
            log.warn("Phone already registered - Phone: {}", registerRequest.getPhone());
            throw new BusinessException("该手机号已注册");
        }
        
        // 3. 检查用户名是否已存在
        if (userDao.existsByUsername(registerRequest.getNickname())) {
            log.warn("Username already exists - Nickname: {}", registerRequest.getNickname());
            throw new BusinessException("该用户名已被占用");
        }
        
        // 4. 创建用户
        User newUser = createNewUser(registerRequest, ipAddress);
        
        // 5. 保存用户
        User savedUser = userDao.save(newUser);
        
        // 6. 生成令牌（注册即登录）
        String accessToken = tokenUtil.generateAccessToken(savedUser);
        String refreshToken = tokenUtil.generateRefreshToken(savedUser);
        
        // 7. 创建会话
        UserSession session = createUserSession(savedUser, accessToken, refreshToken, ipAddress, userAgent);
        sessionDao.saveSession(session);
        
        // 8. 标记验证码为已使用
        captchaDao.markCaptchaAsUsed(registerRequest.getPhone(), registerRequest.getCaptcha(), "REGISTER");
        
        // 9. 记录注册日志
        logRegistration(savedUser.getId(), registerRequest.getPhone(), ipAddress, userAgent);
        
        log.info("Registration successful - User ID: {}, Phone: {}", savedUser.getId(), registerRequest.getPhone());
        
        // 10. 返回注册响应
        return authConverter.toRegisterVO(
            savedUser,
            accessToken,
            refreshToken,
            "注册成功",
            session.getExpiresAt()
        );
    }
    
    @Override
    public void logout(String accessToken, String ipAddress) {
        log.info("Logout attempt - Access Token: {}, IP: {}", accessToken.substring(0, Math.min(20, accessToken.length())), ipAddress);
        
        // 1. 验证令牌
        Optional<UserSession> sessionOpt = sessionDao.findByAccessToken(accessToken);
        if (sessionOpt.isEmpty()) {
            log.warn("Session not found for logout");
            return;
        }
        
        UserSession session = sessionOpt.get();
        
        // 2. 标记会话为登出
        session.setStatus(UserSession.SessionStatus.LOGOUT);
        sessionDao.saveSession(session);
        
        // 3. 更新用户登出时间
        userDao.updateLogoutInfo(session.getUserId(), LocalDateTime.now());
        
        // 4. 记录登出日志
        logLogout(session.getUserId(), ipAddress, "手动登出");
        
        log.info("Logout successful - User ID: {}", session.getUserId());
    }
    
    @Override
    public void logoutAllSessions(Long userId, String ipAddress) {
        log.info("Logout all sessions - User ID: {}, IP: {}", userId, ipAddress);
        
        // 1. 撤销所有活跃会话
        sessionDao.revokeAllSessions(userId);
        
        // 2. 更新用户登出时间
        userDao.updateLogoutInfo(userId, LocalDateTime.now());
        
        // 3. 记录登出日志
        logLogout(userId, ipAddress, "全部设备登出");
        
        log.info("All sessions logged out - User ID: {}", userId);
    }
    
    @Override
    public CaptchaResponseVO generateCaptcha(CaptchaRequestDTO captchaRequest, String ipAddress) {
        log.info("Generate captcha request - Phone: {}, IP: {}", captchaRequest.getPhone(), ipAddress);
        
        // 1. 频率限制检查
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        int recentCount = captchaDao.getRecentCaptchaCount(captchaRequest.getPhone(), oneMinuteAgo);
        
        if (recentCount >= 3) {
            log.warn("Too many captcha requests - Phone: {}, Count: {}", captchaRequest.getPhone(), recentCount);
            throw new BusinessException("验证码请求过于频繁，请稍后再试");
        }
        
        // 2. 检查用户是否存在（如果是登录场景）
        Optional<User> userOptional = userDao.findByPhone(captchaRequest.getPhone());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 如果是注册请求但用户已存在，拒绝
            if (!passwordUtil.verifyPassword(captchaRequest.getPassword(), user.getPasswordHash(), user.getSalt())) {
                log.warn("Invalid password for captcha request - Phone: {}", captchaRequest.getPhone());
                throw new BusinessException("密码错误");
            }
        }
        
        // 3. 生成验证码
        String captcha = captchaUtil.generateCaptcha(6);
        
        // 4. 设置过期时间
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(CAPTCHA_EXPIRE_MINUTES);
        
        // 5. 保存验证码（假设是登录验证码）
        captchaDao.saveCaptcha(captchaRequest.getPhone(), captcha, "LOGIN", ipAddress, expireTime);
        
        // 6. 清理过期验证码
        captchaDao.deleteExpiredCaptchas();
        
        log.info("Captcha generated - Phone: {}, Captcha: {}", captchaRequest.getPhone(), captcha);
        
        // 7. 返回验证码（在实际应用中，应该通过短信发送，这里只返回用于测试）
        return authConverter.toCaptchaVO(captcha, expireTime);
    }
    
    @Override
    public TokenRefreshResponseVO refreshToken(String refreshToken, String ipAddress) {
        log.info("Token refresh request - IP: {}", ipAddress);
        
        // 1. 验证刷新令牌
        Optional<UserSession> sessionOpt = sessionDao.findByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty() || !sessionOpt.get().isValid()) {
            log.warn("Invalid refresh token");
            throw new BusinessException("刷新令牌无效或已过期");
        }
        
        UserSession oldSession = sessionOpt.get();
        
        // 2. 获取用户信息
        Optional<User> userOpt = userDao.findById(oldSession.getUserId());
        if (userOpt.isEmpty()) {
            log.warn("User not found for refresh token - User ID: {}", oldSession.getUserId());
            throw new BusinessException("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 3. 生成新的令牌
        String newAccessToken = tokenUtil.generateAccessToken(user);
        String newRefreshToken = tokenUtil.generateRefreshToken(user);
        
        // 4. 创建新会话
        UserSession newSession = createUserSession(user, newAccessToken, newRefreshToken, ipAddress, oldSession.getUserAgent());
        sessionDao.saveSession(newSession);
        
        // 5. 标记旧会话为已过期
        oldSession.setStatus(UserSession.SessionStatus.EXPIRED);
        sessionDao.saveSession(oldSession);
        
        // 6. 记录令牌刷新日志
        logTokenRefresh(user.getId(), ipAddress);
        
        log.info("Token refreshed - User ID: {}", user.getId());
        
        // 7. 返回新令牌
        return TokenRefreshResponseVO.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresAt(newSession.getExpiresAt())
            .message("令牌刷新成功")
            .build();
    }
    
    @Override
    public boolean validateCaptcha(String phone, String captcha, String captchaType) {
        Optional<String> validCaptcha = captchaDao.getValidCaptcha(phone, captchaType);
        return validCaptcha.isPresent() && validCaptcha.get().equals(captcha);
    }
    
    // ==================== 私有辅助方法 ====================
    
    private User createNewUser(RegisterRequestDTO request, String ipAddress) {
        User newUser = new RegularUser();
        
        // 基本信息
        newUser.setUsername(request.getNickname());
        newUser.setNickname(request.getNickname());
        newUser.setPhone(request.getPhone());
        newUser.setEmail(generateEmail(request.getPhone()));
        newUser.setAvatarUrl("/default-avatar.png");
        newUser.setBio("欢迎来到EAZ！");
        
        // 密码加密
        String salt = passwordUtil.generateSalt();
        String passwordHash = passwordUtil.hashPassword(request.getPassword(), salt);
        newUser.setPasswordHash(passwordHash);
        newUser.setSalt(salt);
        
        // 状态和时间
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setUserType(UserType.REGULAR);
        newUser.setRegistrationIp(ipAddress);
        newUser.setRegistrationTime(LocalDateTime.now());
        newUser.setLastLoginAt(LocalDateTime.now());
        newUser.setCreatedAt(LocalDateTime.now());
        
        // 安全设置
        newUser.setRequireCaptcha(true);
        newUser.setTwoFactorEnabled(false);
        
        return newUser;
    }
    
    private UserSession createUserSession(User user, String accessToken, String refreshToken, 
                                         String ipAddress, String userAgent) {
        UserSession session = new UserSession();
        
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(user.getId());
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setDeviceInfo("Web Browser");
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setLoginTime(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusHours(SESSION_EXPIRE_HOURS));
        session.setStatus(UserSession.SessionStatus.ACTIVE);
        
        return session;
    }
    
    private String generateEmail(String phone) {
        return phone + "@eaz.com";
    }
    
    private void logLogin(Long userId, String phone, boolean success, String ipAddress, String userAgent, String loginType) {
        // 在实际应用中，这里应该记录到login_logs表
        log.info("Login {} - User ID: {}, Phone: {}, IP: {}, Type: {}", 
                success ? "successful" : "failed", userId, phone, ipAddress, loginType);
    }
    
    private void logRegistration(Long userId, String phone, String ipAddress, String userAgent) {
        // 在实际应用中，这里应该记录到专门的注册日志表
        log.info("Registration - User ID: {}, Phone: {}, IP: {}", userId, phone, ipAddress);
    }
    
    private void logLogout(Long userId, String ipAddress, String logoutType) {
        log.info("Logout - User ID: {}, IP: {}, Type: {}", userId, ipAddress, logoutType);
    }
    
    private void logTokenRefresh(Long userId, String ipAddress) {
        log.info("Token refresh - User ID: {}, IP: {}", userId, ipAddress);
    }
}