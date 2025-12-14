package com.exdemix.backend.service;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    void login();

    void register();

    void logout();

    void fetchVerifyCode();
}