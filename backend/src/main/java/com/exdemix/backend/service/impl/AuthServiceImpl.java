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
        this.userDao = new UserDaoImpl(); // 使用基于数据库的实现
        this.authConverter = new AuthConverter();
    }

    @Override
    public LoginResponseVO login(LoginRequestDTO loginRequest) {
        // 1. 验证验证码
        if (!"123456".equals(loginRequest.getCaptcha())) {
            throw new RuntimeException("Captcha is incorrect");
        }

        // 2. 根据手机号查找用户
        Optional<User> userOptional = userDao.findByPhone(loginRequest.getPhone());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }

        User user = userOptional.get();

        // 3. 验证密码（这里简化处理，实际应使用加密验证）
        if (!verifyPassword(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Password is incorrect");
        }

        // 4. 生成访问令牌（这里简化处理）
        String token = generateToken(user);

        // 5. 更新最后登录时间并将用户状态设为活跃
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
            throw new RuntimeException("Captcha is incorrect");
        }

        // 2. 检查手机号是否已注册
        Optional<User> existingUser = userDao.findByPhone(registerRequest.getPhone());
        if (existingUser.isPresent()) {
            // 检查用户名是否也被占用
            Optional<User> userWithSameUsername = userDao.findByUsername(registerRequest.getUsername() + " User");
            if (userWithSameUsername.isPresent()) {
                throw new RuntimeException("Phone number and username have been registered, please login directly or use another phone number and username");
            } else {
                throw new RuntimeException("Phone number has been registered, please login directly or use another phone number");
            }
        }

        // 3. 检查用户名是否已存在
        if (userDao.findByUsername(registerRequest.getUsername() + " User").isPresent()) {
            throw new RuntimeException("Username has been occupied, please choose another username");
        }

        // 4. 创建新用户
        User newUser = new RegularUser();
        newUser.setUsername(registerRequest.getUsername() + " User");
        newUser.setNickname(registerRequest.getUsername());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setEmail(generateEmail()); // 生成唯一的邮箱
        newUser.setPasswordHash(registerRequest.getPassword()); // 暂时明文
        newUser.setUserType(UserType.REGULAR);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setLastLoginAt(LocalDateTime.now());

        String token = generateToken(newUser);

        // 5. 保存用户
        User savedUser = userDao.save(newUser);

        // 6. 转换为响应 VO
        return authConverter.toRegisterVO(savedUser, token, "Registration successful!");
    }

    /**
     * 生成唯一的邮箱地址
     * @return 唯一的邮箱地址
     */
    private String generateEmail() {
        // 使用当前时间戳生成唯一的邮箱地址
        return System.currentTimeMillis() + "@example.com";
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