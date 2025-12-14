package com.exdemix.backend.servlet.wallpaper;

import com.exdemix.backend.service.CarouselService;
import com.exdemix.backend.service.WallpaperService;
import com.exdemix.backend.service.impl.CarouselServiceImpl;
import com.exdemix.backend.service.impl.WallpaperServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/wallpaper/carousel")
public class CarouselWallpaperServlet extends HttpServlet {

    private final CarouselService carouselService;

    public CarouselWallpaperServlet() {
        this.carouselService = new CarouselServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        wallpaperService.getCarousel();
    }
}