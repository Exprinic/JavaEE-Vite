package com.exdemix.backend.service.impl;

import com.exdemix.backend.dao.WallpaperDao;
import com.exdemix.backend.dao.impl.WallpaperDaoImpl;
import com.exdemix.backend.dto.SearchRequestDTO;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.entity.wallpaper.WallpaperStatus;
import com.exdemix.backend.service.WallpaperService;
import com.exdemix.backend.util.GsonUtil;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Slf4j
public class WallpaperServiceImpl implements WallpaperService {
    private final WallpaperDaoImpl wallpaperDao = new WallpaperDaoImpl(); // 使用具体实现类以便调用新增方法
    private final Gson gson = GsonUtil.getGson();

    @Override
    public void upload() {

    }

    @Override
    public void getDetail() {

    }

    @Override
    public void download() {

    }

    @Override
    public List<Wallpaper> advancedSearch(SearchRequestDTO searchRequest) {
        try {
            // 获取搜索参数
            String query = searchRequest.getQ();
            String category = searchRequest.getCategory();
            
            // URL解码
            if (query != null) {
                query = URLDecoder.decode(query, StandardCharsets.UTF_8);
            }
            
            if (category != null) {
                category = URLDecoder.decode(category, StandardCharsets.UTF_8);
            }
            
            List<Wallpaper> wallpapers;
            
            // 根据参数执行不同的搜索逻辑
            boolean choice = category != null && !category.isEmpty() && !"ALL".equals(category);
            if (query != null && !query.isEmpty()) {
                // 如果有搜索词，执行关键词搜索
                if (query.startsWith("#")) {
                    // 标签搜索
                    String tag = query.substring(1);
                    if (choice) {
                        // 标签+分类联合搜索
                        wallpapers = wallpaperDao.findByTagAndCategory(tag, category);
                    } else {
                        // 仅标签搜索
                        wallpapers = wallpaperDao.findByTag(tag);
                    }
                } else {
                    // 普通关键词搜索
                    if (choice) {
                        // 关键词+分类联合搜索
                        wallpapers = wallpaperDao.findByKeywordAndCategory(query, category);
                    } else {
                        // 仅关键词搜索
                        wallpapers = wallpaperDao.findByKeyword(query);
                    }
                }
            } else if (choice) {
                // 仅分类搜索
                wallpapers = wallpaperDao.findByCategory(category);
            } else {
                // 获取所有壁纸
                wallpapers = wallpaperDao.findAll();
            }
            
            return wallpapers;
        } catch (Exception e) {
            log.error("Failed to search wallpapers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search wallpapers: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Wallpaper> getAllWallpapers() {
        try {
            return wallpaperDao.findAll();
        } catch (Exception e) {
            log.error("Failed to fetch wallpapers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch wallpapers: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Wallpaper> getCarouselWallpapers() {
        try {
            return wallpaperDao.findWallpapersWidthIsGreaterThanHeight();
        } catch (Exception e) {
            log.error("Failed to fetch carousel wallpapers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch carousel wallpapers: " + e.getMessage(), e);
        }
    }
}