package com.exdemix.backend.service.impl;

import com.exdemix.backend.dao.impl.UserDaoImpl;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDaoImpl userDao = new UserDaoImpl();

    /**
     * 根据用户ID获取用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称，如果未找到则返回"Unknown"
     */
    public String getUserNameById(Long userId) {
        try {
            if (userId == null) {
                return "Unknown";
            }
            
            Optional<User> userOptional = userDao.findById(userId);
            return userOptional.map(User::getNickname).orElse("Unknown");
        } catch (Exception e) {
            log.error("Failed to fetch user by id: {}", userId, e);
            return "Unknown";
        }
    }

    @Override
    public void getProfile() {

    }

    @Override
    public void updateProfile() {

    }

    @Override
    public void changePassword() {

    }

    @Override
    public void getUploadHistory() {

    }
}