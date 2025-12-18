// TokenRefreshResponseVO.java
package com.exdemix.backend.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TokenRefreshResponseVO {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private String message;
}