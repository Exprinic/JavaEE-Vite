package com.exdemix.backend.service.impl;

import com.exdemix.backend.service.UserService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private final Gson gson = new Gson();

    @Override
    public void getProfile() {

    }

    @Override
    public void updateProfile() {

    }

    @Override
    public void changePassword() {

    }

    @Override
    public void getUploadHistory() {

    }
}