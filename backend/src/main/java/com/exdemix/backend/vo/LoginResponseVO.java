package com.exdemix.backend.vo;

import com.exdemix.backend.entity.user.Permission;
import com.exdemix.backend.entity.user.RegularUser;
import com.exdemix.backend.entity.user.UserType;
import jakarta.mail.internet.HeaderTokenizer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO extends RegularUser {
    private Long userId;
    private HeaderTokenizer.Token token;
    private String username;
    private String nickname;
    private String avatar;

    private UserType userType;
    private String accessToken;
    private Set<Permission> permissions;
    private List<String> roles;

    private LocalDateTime loginTime;
}
