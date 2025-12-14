package com.exdemix.backend.dao;

import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.entity.wallpaper.WallpaperStatus;

import java.util.List;

public interface WallpaperDao extends GenericDao<Wallpaper, Long> {
    List<Wallpaper> findByStatus(WallpaperStatus status);
    List<Wallpaper> findByUploaderId(Long uploaderId);
}