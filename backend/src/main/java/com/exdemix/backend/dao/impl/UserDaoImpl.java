package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.UserDao;
import com.exdemix.backend.entity.user.Permission;
import com.exdemix.backend.entity.user.RegularUser;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.entity.user.UserType;
import com.exdemix.backend.vo.LoginResponseVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserDaoImpl implements UserDao {
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
//        return LoginResponseVO.builder()
//                .userId(user.getId())
//                .username(user.getPhone())
//                .nickname(user.getNickname())
//                .avatar(user.getAvatarUrl())
//                .userType(user.getUserType())
//                .accessToken(token)
//                .permissions(user.getPermissions())
//                .roles(getRoleNames(user.getRoles())) // 使用新方法
//                .loginTime(LocalDateTime.now())
//                .build();
        // 根据如上返回类型设置一个临时RegularUser 对象，用于测试
        RegularUser user = new RegularUser();
        user.setId(1L);
        user.setPhone(phone);
        user.setNickname("Test User");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setPasswordHash("temporaryPassword");
        user.setUserType(UserType.REGULAR);
        user.setPermissions(Set.of(Permission.UPLOAD_WALLPAPER));
        user.setLastLoginAt(LocalDateTime.now());
        return Optional.of(user);
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
    public User save(User entity) {
        return null;
    }

    @Override
    public void update(User entity) {
        System.out.println(entity.getLastLoginAt());
    }

    @Override
    public void deleteById(Long aLong) {

    }
}
