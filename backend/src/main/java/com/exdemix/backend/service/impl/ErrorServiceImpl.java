package com.exdemix.backend.service.impl;

import com.exdemix.backend.service.ErrorService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class ErrorServiceImpl implements ErrorService {

    private final Gson gson = new Gson();

    @Override
    public void report() {

    }
}