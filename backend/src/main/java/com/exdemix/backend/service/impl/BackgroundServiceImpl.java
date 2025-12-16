package com.exdemix.backend.service.impl;

import com.exdemix.backend.service.BackgroundService;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackgroundServiceImpl implements BackgroundService {
    

    @Override
    public String getDefaultBackground() {
        return "default";
    }

    @Override
    public String getBackground(int id) {
        return "background" + id;
    }
    @Override
    public List<String> getBackgroundHistory() {
        return List.of("background1", "background2", "background3");
    }
    @Override
    public void setBackground(int id) {
        // 模拟设置背景
        log.info("Setting background to {}", getBackground(id));
    }
}