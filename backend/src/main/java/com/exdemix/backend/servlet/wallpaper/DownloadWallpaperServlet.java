package com.exdemix.backend.servlet.wallpaper;

import com.exdemix.backend.service.WallpaperService;
import com.exdemix.backend.service.impl.WallpaperServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/wallpaper/download")
public class DownloadWallpaperServlet extends HttpServlet {

    private final WallpaperService wallpaperService;

    public DownloadWallpaperServlet() {
        this.wallpaperService = new WallpaperServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        wallpaperService.download();
    }
}