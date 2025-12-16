package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.CarouselDao;
import com.exdemix.backend.dao.WallpaperDao;
import com.exdemix.backend.entity.wallpaper.Wallpaper;

import java.util.List;

public class CarouselDaoImpl implements CarouselDao {
    private final WallpaperDao wallpaperDao = new WallpaperDaoImpl();

    @Override
    public List<Wallpaper> findAllCarousels() {
        return wallpaperDao.findAll();
    }

    @Override
    public void addCarouselImageByWallpaperId(int wallpaperId) {

    }

    @Override
    public void deleteCarouselImageById(int carouselId) {

    }
}