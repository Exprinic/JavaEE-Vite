package com.exdemix.backend.servlet.wallpaper;

import com.exdemix.backend.service.WallpaperService;
import com.exdemix.backend.service.impl.WallpaperServiceImpl;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.vo.ApiResponse;
import com.exdemix.backend.util.GsonUtil;
import com.exdemix.backend.converter.WallpaperConverter;
import com.exdemix.backend.vo.WallpaperVO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/wallpaper/detail")
public class WallpaperDetailServlet extends HttpServlet {

    private final WallpaperService wallpaperService;
    private final Gson gson;

    public WallpaperDetailServlet() {
        this.wallpaperService = new WallpaperServiceImpl();
        this.gson = GsonUtil.getGson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 从请求参数中获取壁纸ID
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Missing wallpaper ID");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(errorResponse));
                return;
            }

            Long id = Long.parseLong(idParam);
            Optional<Wallpaper> wallpaperOptional = wallpaperService.getWallpaperById(id);
            
            if (wallpaperOptional.isPresent()) {
                Wallpaper wallpaper = wallpaperOptional.get();
                WallpaperVO wallpaperVO = WallpaperConverter.toVO(wallpaper);
                
                ApiResponse<WallpaperVO> apiResponse = ApiResponse.success(wallpaperVO);
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(apiResponse));
            } else {
                ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_NOT_FOUND, "Wallpaper not found");
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(errorResponse));
            }
        } catch (NumberFormatException e) {
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_BAD_REQUEST, "Invalid wallpaper ID format");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<String> errorResponse = ApiResponse.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch wallpaper details: " + e.getMessage());
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorResponse));
        }
    }
}