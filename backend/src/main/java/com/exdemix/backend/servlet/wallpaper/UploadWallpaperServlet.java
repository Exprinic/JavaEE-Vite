package com.exdemix.backend.servlet.wallpaper;

import com.exdemix.backend.service.WallpaperService;
import com.exdemix.backend.service.impl.WallpaperServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/wallpaper/upload")
public class UploadWallpaperServlet extends HttpServlet {

    private final WallpaperService wallpaperService;

    public UploadWallpaperServlet() {
        this.wallpaperService = new WallpaperServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        wallpaperService.upload();
    }
}