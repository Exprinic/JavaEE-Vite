package com.exdemix.backend.converter;

import com.exdemix.backend.entity.user.Role;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.vo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AuthConverter {

    // 通用转换方法 - 支持所有User子类
    public <T extends User> LoginResponseVO toLoginVO(
            T user, String accessToken,String refreshToken,String message) {
        if (user == null) return null;

        Long expireIn = 3600L;

        return LoginResponseVO.builder()
                .userInfo(UserInfoVO.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatarUrl())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .userType(user.getUserType())
                        .build())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expireIn)
                .permissions(user.getPermissions())
                .roles(getRoleNames(user.getRoles())) // 使用新方法
                .message(message)
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
                .map(user -> toLoginVO(user, null, null, "Login successful!"))
                .collect(Collectors.toList());
    }

    public RegisterResponseVO toRegisterVO(User user, String accessToken,String refreshToken,String message) {
        if (user == null)
            return null;

        Long expireIn = 3600L;

        return RegisterResponseVO.builder()
                .userInfo(UserInfoVO.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatarUrl())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .userType(user.getUserType())
                        .build())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expireIn)
                .message(message)
                .permissions(user.getPermissions())
                .roles(getRoleNames(user.getRoles()))
                .registerMessage("Register successful!")
                .build();
    }

    public CaptchaResponseVO toCaptchaVO(String captcha) {
        return CaptchaResponseVO.builder()
                .captcha(captcha)
                .build();
    }
    public LogoutResponseVO toLogoutVO(String message) {
        return LogoutResponseVO.builder()
                .message(message)
                .build();
    }
}