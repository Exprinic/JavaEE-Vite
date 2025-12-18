package com.exdemix.backend.service;

import com.exdemix.backend.dto.SearchRequestDTO;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.vo.WallpaperVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WallpaperService {
    void upload();
    void getDetail();
    void download();
    
    // 更新方法签名，使用DTO而不是直接处理HTTP请求
    List<Wallpaper> advancedSearch(SearchRequestDTO searchRequest);
    
    List<Wallpaper> getAllWallpapers();
    
    // 添加获取轮播图壁纸的方法
    List<Wallpaper> getCarouselWallpapers();
    
    // 添加根据ID获取壁纸详情的方法
    Optional<Wallpaper> getWallpaperById(Long id);
}