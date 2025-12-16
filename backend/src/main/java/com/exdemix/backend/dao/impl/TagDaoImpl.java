package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.TagDao;
import com.exdemix.backend.entity.tag.Tag;

import java.util.List;

public class TagDaoImpl implements TagDao {
    @Override
    public List<Tag> findAll() {
        return null;
    }
    @Override
    public List<Tag> findTagsByWallpaperId(int wallpaperId) {
        return null;
    }
    @Override
    public boolean findTagsByTagName(String tagName) {
        return false;
    }
    @Override
    public void addTags(List<Tag> tags) {

    }
    @Override
    public void deleteTagsByTagName(String tagName) {

    }
}
