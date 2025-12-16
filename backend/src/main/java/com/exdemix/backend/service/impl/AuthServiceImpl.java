package com.exdemix.backend.service.impl;

import com.exdemix.backend.converter.AuthConverter;
import com.exdemix.backend.dao.UserDao;
import com.exdemix.backend.dao.impl.UserDaoImpl;
import com.exdemix.backend.dto.CaptchaRequestDTO;
import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.dto.LogoutRequestDTO;
import com.exdemix.backend.dto.RegisterRequestDTO;
import com.exdemix.backend.entity.user.RegularUser;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.entity.user.UserStatus;
import com.exdemix.backend.entity.user.UserType;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.vo.CaptchaResponseVO;
import com.exdemix.backend.vo.LoginResponseVO;
import com.exdemix.backend.vo.LogoutResponseVO;
import com.exdemix.backend.vo.RegisterResponseVO;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final AuthConverter authConverter;

    public AuthServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.authConverter = new AuthConverter();
    }

    @Override
    public LoginResponseVO login(LoginRequestDTO loginRequest) {
        // 1. 验证验证码
        if (!"123456".equals(loginRequest.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }

        // 2. 根据手机号查找用户
        Optional<User> userOptional = userDao.findByPhone(loginRequest.getPhone());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOptional.get();

        // 3. 验证密码（这里简化处理，实际应使用加密验证）
        if (!verifyPassword(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }

        // 4. 生成访问令牌（这里简化处理）
        String token = generateToken(user);

        // 5. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        userDao.update(user);

        return authConverter.toLoginVO(user, token, "Login successful!");
    }

    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        // 实际应使用 BCrypt 等加密算法验证
        return rawPassword.equals(hashedPassword); // 简化示例
    }

    private String generateToken(User user) {
        // 实际应生成 JWT token
        return "generated_token_for_user_" + user.getId(); // 简化示例
    }

    @Override
    public RegisterResponseVO register(RegisterRequestDTO registerRequest) {
        // 1. 验证验证码
        if (!"123456".equals(registerRequest.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }

        // 2. 检查手机号是否已注册
        if (userDao.findByPhone(registerRequest.getPhone()).isPresent()) {
            throw new RuntimeException("手机号已被注册");
        }

        // 3. 创建新用户
        User newUser = new RegularUser();
        newUser.setUsername(registerRequest.getUsername() + " User");
        newUser.setNickname(registerRequest.getUsername());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setPasswordHash(registerRequest.getPassword()); // 暂时明文
        newUser.setUserType(UserType.REGULAR);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());

        String token = generateToken(newUser);

        // 4. 保存用户
        User savedUser = userDao.save(newUser);

        // 5. 转换为响应 VO
        return authConverter.toRegisterVO(savedUser, token, "Registration successful!");
    }

    @Override
    public LogoutResponseVO logout(LogoutRequestDTO logoutRequest) {
        // 登出逻辑
        Optional<User> userOptional = userDao.findByPhone(logoutRequest.getPhone());
        if (userOptional.isEmpty()) {
            // 用户不存在时也返回成功，因为可能是重复登出或其他情况
            return authConverter.toLogoutVO("Logout successful!");
        }

        User user = userOptional.get();

        // 更新最后登出时间
        user.setLastLogoutAt(LocalDateTime.now());
        user.setStatus(UserStatus.SUSPENDED);
        userDao.update(user);

        // 转换为响应 VO
        return authConverter.toLogoutVO("Logout successful!");
    }

    @Override
    public CaptchaResponseVO generateCaptcha(CaptchaRequestDTO captchaRequestDTO) {
        // 生成验证码逻辑
        return authConverter.toCaptchaVO("123456"); // 简化示例
    }
}