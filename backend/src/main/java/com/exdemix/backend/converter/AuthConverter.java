package com.exdemix.backend.converter;

import com.exdemix.backend.entity.user.Role;
import com.exdemix.backend.entity.user.User;
import com.exdemix.backend.vo.CaptchaResponseVO;
import com.exdemix.backend.vo.LoginResponseVO;
import com.exdemix.backend.vo.LogoutResponseVO;
import com.exdemix.backend.vo.RegisterResponseVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AuthConverter {

    // 通用转换方法 - 支持所有User子类
    public <T extends User> LoginResponseVO toLoginVO(T user, String token,String message) {
        if (user == null) return null;

        return LoginResponseVO.builder()
                .userId(user.getId())
                .username(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatarUrl())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .accessToken(token)
                .permissions(user.getPermissions())
                .roles(getRoleNames(user.getRoles())) // 使用新方法
                .loginTime(LocalDateTime.now())
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
                .map(user -> toLoginVO(user, null, "Login successful!"))
                .collect(Collectors.toList());
    }

    public RegisterResponseVO toRegisterVO(User user, String token,String message) {
        if (user == null)
            return null;
        return RegisterResponseVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .status(user.getStatus())
                .userType(user.getUserType())
                .permissions(user.getPermissions())
                .message(message)
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