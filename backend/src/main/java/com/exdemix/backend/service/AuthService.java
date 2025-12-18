// AuthService.java (接口)
package com.exdemix.backend.service;

import com.exdemix.backend.dto.*;
import com.exdemix.backend.vo.*;

public interface AuthService {
    LoginResponseVO login(LoginRequestDTO loginRequest, String ipAddress, String userAgent) throws InterruptedException;
    RegisterResponseVO register(RegisterRequestDTO registerRequest, String ipAddress, String userAgent);
    void logout(String accessToken, String ipAddress);
    void logoutAllSessions(Long userId, String ipAddress);
    CaptchaResponseVO generateCaptcha(CaptchaRequestDTO captchaRequest, String ipAddress);
    TokenRefreshResponseVO refreshToken(String refreshToken, String ipAddress);
    boolean validateCaptcha(String phone, String captcha, String captchaType);
}