package com.exdemix.backend.dao;

import com.exdemix.backend.entity.wallpaper.Wallpaper;

import java.util.List;

public interface CarouselDao {
    List<Wallpaper> findAllCarousels();
    void addCarouselImageByWallpaperId(int wallpaperId);
    void deleteCarouselImageById(int carouselId);
}