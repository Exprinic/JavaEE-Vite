package com.exdemix.backend.service;

import com.exdemix.backend.dto.CaptchaRequestDTO;
import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.dto.LogoutRequestDTO;
import com.exdemix.backend.dto.RegisterRequestDTO;
import com.exdemix.backend.vo.CaptchaResponseVO;
import com.exdemix.backend.vo.LoginResponseVO;
import com.exdemix.backend.vo.LogoutResponseVO;
import com.exdemix.backend.vo.RegisterResponseVO;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    LoginResponseVO login(LoginRequestDTO loginRequest);

    RegisterResponseVO register(RegisterRequestDTO registerRequest);

    LogoutResponseVO logout(LogoutRequestDTO logoutRequest);

    CaptchaResponseVO generateCaptcha(CaptchaRequestDTO captchaRequestDTO);
}