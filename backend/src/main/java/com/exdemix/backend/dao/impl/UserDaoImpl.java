package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.UserDao;
import com.exdemix.backend.entity.user.Permission;
import com.exdemix.backend.entity.user.RegularUser;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.entity.user.UserType;
import com.exdemix.backend.vo.LoginResponseVO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class UserDaoImpl implements UserDao {
    // 临时实现，仅用于测试
    private final List<User> users = new ArrayList<>(List.of(
            new RegularUser() {{
                setId(1L);
                setPhone("13800000000");
                setNickname("Test User");
                setAvatarUrl("https://example.com/avatar.jpg");
                setPasswordHash("Test123!");
                setUserType(UserType.REGULAR);
                setPermissions(Set.of(Permission.UPLOAD_WALLPAPER));
                setLastLoginAt(LocalDateTime.now());
            }}
    ));

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(String phone) {

        // 临时实现，仅用于测试
        for(User user : users) {
            if(user.getPhone().equals(phone)) {
                log.info("{} {} has been found", user.getCreatedAt(), user.getNickname());
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User save(User user) {
        log.info("{} {} has been saved", user.getCreatedAt(), user.getNickname());
        users.add(user);
        return user;
    }

    @Override
    public void update(User entity) {
        log.info("{} {} has been updated", entity.getLastLoginAt(), entity.getNickname());
    }

    @Override
    public void deleteById(Long aLong) {

    }
}