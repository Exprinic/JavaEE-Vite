package com.exdemix.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UserService {

    void getProfile();

    void updateProfile();

    void changePassword();

    void getUploadHistory();
}