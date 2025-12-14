package com.exdemix.backend.service.impl;

import com.exdemix.backend.service.AuthService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    private final Gson gson = new Gson();

    @Override
    public void login() {
        // Placeholder for login logic
        System.out.println("Login successful");
    }

    @Override
    public void register() {
        // Placeholder for registration logic
        System.out.println("Registration successful");
    }

    @Override
    public void logout() {
        // Placeholder for logout logic
        System.out.println("Logout successful");
    }

    @Override
    public void fetchVerifyCode() {
        // Placeholder for verification code logic
        System.out.println("Verification code sent");
    }
}