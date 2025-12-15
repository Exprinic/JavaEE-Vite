package com.exdemix.backend.service;

import com.exdemix.backend.dto.LoginRequestDTO;
import com.exdemix.backend.vo.LoginResponseVO;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    LoginResponseVO login(LoginRequestDTO loginRequest);

    void register();

    void logout();

    void fetchVerifyCode();
}