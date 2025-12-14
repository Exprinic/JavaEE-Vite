package com.exdemix.backend.service.impl;

import com.exdemix.backend.dao.BackgroundDao;
import com.exdemix.backend.dao.impl.BackgroundDaoImpl;
import com.exdemix.backend.service.BackgroundService;

import java.util.List;

public class BackgroundServiceImpl implements BackgroundService {

    private final BackgroundDao backgroundDao = new BackgroundDaoImpl();

    @Override
    public String getDefaultBackground() {
        return backgroundDao.findAll().get(0).toString();
    }

    @Override
    public String getBackground(int id) {
        return backgroundDao.findBackgroundById(id).toString();
    }
    @Override
    public List<String> getBackgroundHistory() {
        return backgroundDao.findAll();
    }
    @Override
    public void setBackground(int id) {
        backgroundDao.findBackgroundById(id);
    }
}