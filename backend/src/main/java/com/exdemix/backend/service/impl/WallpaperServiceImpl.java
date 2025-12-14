package com.exdemix.backend.service.impl;

import com.exdemix.backend.dao.WallpaperDao;
import com.exdemix.backend.dao.impl.WallpaperDaoImpl;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.service.WallpaperService;

import java.util.List;

public class WallpaperServiceImpl implements WallpaperService {
    private final WallpaperDao wallpaperDao = new WallpaperDaoImpl();

    @Override
    public List<Wallpaper> list() {
        return wallpaperDao.findAll();
    }

    @Override
    public Wallpaper getById(Long id) {
        return wallpaperDao.findById(id);
    }
}