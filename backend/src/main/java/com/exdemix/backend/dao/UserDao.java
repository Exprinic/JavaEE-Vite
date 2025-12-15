package com.exdemix.backend.dao;

import com.exdemix.backend.entity.user.User;

import java.util.Optional;

public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone); // 新增方法
}
