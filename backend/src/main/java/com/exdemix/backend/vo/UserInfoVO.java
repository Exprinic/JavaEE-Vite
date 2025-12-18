package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.UserStatus;
import com.exdemix.backend.entity.user.UserType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfoVO {
    private Long userId;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private String bio;

    private UserType userType;
    private UserStatus status;
}
