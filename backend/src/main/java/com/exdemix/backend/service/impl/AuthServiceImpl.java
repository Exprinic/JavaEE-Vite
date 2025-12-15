package com.exdemix.backend.service.impl;

import com.exdemix.backend.converter.UserConverter;
import com.exdemix.backend.dao.UserDao;
import com.exdemix.backend.dao.impl.UserDaoImpl;
import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.service.AuthService;
import com.exdemix.backend.vo.LoginResponseVO;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final UserConverter userConverter;

    public AuthServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.userConverter = new UserConverter();
    }

    @Override
    public LoginResponseVO login(LoginRequestDTO loginRequest) {
        // 1. 验证验证码（这里简化处理）
        // validateCaptcha(loginRequest.getCaptcha());

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

        // 5. 转换为响应 VO
        LoginResponseVO responseVO = userConverter.toLoginVO(user, token);

        // 6. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userDao.update(user);

        return responseVO;
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
    public void register() {
        // 注册逻辑
    }

    @Override
    public void logout() {
        // 登出逻辑
    }

    @Override
    public void fetchVerifyCode() {
        // 获取验证码逻辑
    }
}
