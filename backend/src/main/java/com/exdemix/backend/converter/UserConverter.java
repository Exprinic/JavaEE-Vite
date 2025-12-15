package com.exdemix.backend.converter;

import com.exdemix.backend.entity.user.Role;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.vo.LoginResponseVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    // 通用转换方法 - 支持所有User子类
    public <T extends User> LoginResponseVO toLoginVO(T user, String token) {
        if (user == null) return null;

        return LoginResponseVO.builder()
                .userId(user.getId())
                .username(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatarUrl())
                .userType(user.getUserType())
                .accessToken(token)
                .permissions(user.getPermissions())
                .roles(getRoleNames(user.getRoles())) // 使用新方法
                .loginTime(LocalDateTime.now())
                .build();
    }

    private List<String> getRoleNames(List<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // 快速转换List
    public <T extends User> List<LoginResponseVO> toLoginVOList(List<T> users) {
        return users.stream()
                .map(user -> toLoginVO(user, null))
                .collect(Collectors.toList());
    }
}

