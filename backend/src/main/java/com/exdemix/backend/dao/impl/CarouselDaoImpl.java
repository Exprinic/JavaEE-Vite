package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.CarouselDao;
import com.exdemix.backend.dao.WallpaperDao;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.entity.wallpaper.WallpaperStatus;
import com.exdemix.backend.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CarouselDaoImpl implements CarouselDao {
    private final WallpaperDao wallpaperDao = new WallpaperDaoImpl();

    @Override
    public List<Wallpaper> findAllCarousels() {
        // 查询状态为APPROVED的壁纸作为轮播图候选
        String sql = "SELECT * FROM wallpapers WHERE status = 'APPROVED' ORDER BY created_at DESC LIMIT 5";
        List<Wallpaper> carousels = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                carousels.add(mapRowToWallpaper(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding carousel wallpapers", e);
        }
        
        return carousels;
    }

    @Override
    public void addCarouselImageByWallpaperId(int wallpaperId) {
        // 在当前设计中，所有APPROVED状态的壁纸都可以作为轮播图展示
        // 因此不需要额外的添加操作
    }

    @Override
    public void deleteCarouselImageById(int carouselId) {
        // 在当前设计中，通过状态管理轮播图展示
        // 因此不需要额外的删除操作
    }
    
    private Wallpaper mapRowToWallpaper(ResultSet rs) throws SQLException {
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setId(rs.getLong("id"));
        wallpaper.setTitle(rs.getString("title"));
        wallpaper.setDescription(rs.getString("description"));
        
        // 资源URL
        wallpaper.setThumbnailUrl(rs.getString("thumbnail_url"));
        wallpaper.setMediumUrl(rs.getString("medium_url"));
        wallpaper.setFullUrl(rs.getString("full_url"));
        wallpaper.setWatermarkUrl(rs.getString("watermark_url"));
        
        // 状态和其他属性
        wallpaper.setStatus(com.exdemix.backend.entity.wallpaper.WallpaperStatus.valueOf(rs.getString("status")));
        wallpaper.setDeviceType(com.exdemix.backend.entity.wallpaper.DeviceType.valueOf(rs.getString("device_type")));
        wallpaper.setContentRating(com.exdemix.backend.entity.wallpaper.ContentRating.valueOf(rs.getString("content_rating")));
        
        // 时间戳
        // 数据库中字段名为reviewed_at而非review_date
        if (rs.getTimestamp("reviewed_at") != null) {
            wallpaper.setReviewDate(rs.getTimestamp("reviewed_at").toLocalDateTime());
        }
        if (rs.getTimestamp("created_at") != null) {
            wallpaper.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            wallpaper.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        if (rs.getTimestamp("published_at") != null) {
            wallpaper.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
        }
        
        return wallpaper;
    }
}