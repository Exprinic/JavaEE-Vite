package com.exdemix.backend.service;

import java.util.List;

public interface BackgroundService {
    String getBackground(int id);
    List<String> getBackgroundHistory();
    void setBackground(int id);
}