package com.exdemix.backend.service.impl;

import com.exdemix.backend.dao.CarouselDao;
import com.exdemix.backend.dao.impl.CarouselDaoImpl;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.service.CarouselService;

import java.util.List;

public class CarouselServiceImpl implements CarouselService {
    private final CarouselDao carouselDao;

    public CarouselServiceImpl() {
        this.carouselDao = new CarouselDaoImpl();
    }
    
    @Override
    public List<Wallpaper> getCarousels() {
        // 获取轮播图壁纸，选择状态为APPROVED的壁纸
        return carouselDao.findAllCarousels();
    }
}