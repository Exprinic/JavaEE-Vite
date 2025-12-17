package com.exdemix.backend.servlet.wallpaper;

import com.exdemix.backend.service.CarouselService;
import com.exdemix.backend.service.impl.CarouselServiceImpl;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.vo.ApiResponse;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/wallpaper/carousel")
public class CarouselWallpaperServlet extends HttpServlet {

    private final CarouselService carouselService;
    private final Gson gson;

    public CarouselWallpaperServlet() {
        this.carouselService = new CarouselServiceImpl();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Wallpaper> carousels = carouselService.getCarousels();
            ApiResponse<List<Wallpaper>> apiResponse = ApiResponse.success(carousels);
            
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(apiResponse));
        } catch (Exception e) {
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch carousel wallpapers");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}