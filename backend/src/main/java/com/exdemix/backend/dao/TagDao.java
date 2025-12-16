package com.exdemix.backend.dao;

import com.exdemix.backend.entity.tag.Tag;

import java.util.List;

public interface TagDao {
    List<Tag> findAll();
    List<Tag> findTagsByWallpaperId(int wallpaperId);
    boolean findTagsByTagName(String tagName);
    void addTags(List<Tag> tags);
    void deleteTagsByTagName(String tagName);
}
