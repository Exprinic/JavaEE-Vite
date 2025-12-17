package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.WallpaperDao;
import com.exdemix.backend.entity.wallpaper.ContentRating;
import com.exdemix.backend.entity.wallpaper.DeviceType;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.entity.wallpaper.WallpaperStatus;
import com.exdemix.backend.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class WallpaperDaoImpl implements WallpaperDao {

    @Override
    public Optional<Wallpaper> findById(Long id) {
        String sql = "SELECT * FROM wallpapers WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToWallpaper(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding wallpaper by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Wallpaper> findAll() {
        List<Wallpaper> wallpapers = new ArrayList<>();
        String sql = "SELECT * FROM wallpapers";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                wallpapers.add(mapRowToWallpaper(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding all wallpapers", e);
        }
        return wallpapers;
    }

    @Override
    public Wallpaper save(Wallpaper wallpaper) {
        String sql = "INSERT INTO wallpapers (uuid, title, description, thumbnail_url, medium_url, full_url, watermark_url, price, status, device_type, content_rating, uploader_id, created_at, updated_at, published_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, UUID.randomUUID().toString()); // 生成UUID
            stmt.setString(2, wallpaper.getTitle());
            stmt.setString(3, wallpaper.getDescription());
            stmt.setString(4, wallpaper.getThumbnailUrl());
            stmt.setString(5, wallpaper.getMediumUrl());
            stmt.setString(6, wallpaper.getFullUrl());
            stmt.setString(7, wallpaper.getWatermarkUrl());
            stmt.setBigDecimal(8, wallpaper.getPrice());
            stmt.setString(9, wallpaper.getStatus().name());
            stmt.setString(10, wallpaper.getDeviceType().name());
            stmt.setString(11, wallpaper.getContentRating().name());
            stmt.setLong(12, wallpaper.getUploaderId());
            stmt.setTimestamp(13, Timestamp.valueOf(wallpaper.getCreatedAt()));
            stmt.setTimestamp(14, Timestamp.valueOf(wallpaper.getUpdatedAt()));
            stmt.setTimestamp(15, Timestamp.valueOf(wallpaper.getPublishedAt()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        wallpaper.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error saving wallpaper: {}", wallpaper, e);
        }
        return wallpaper;
    }

    @Override
    public void update(Wallpaper wallpaper) {
        String sql = "UPDATE wallpapers SET title = ?, description = ?, thumbnail_url = ?, medium_url = ?, full_url = ?, watermark_url = ?, price = ?, status = ?, device_type = ?, content_rating = ?, uploader_id = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, wallpaper.getTitle());
            stmt.setString(2, wallpaper.getDescription());
            stmt.setString(3, wallpaper.getThumbnailUrl());
            stmt.setString(4, wallpaper.getMediumUrl());
            stmt.setString(5, wallpaper.getFullUrl());
            stmt.setString(6, wallpaper.getWatermarkUrl());
            stmt.setBigDecimal(7, wallpaper.getPrice());
            stmt.setString(8, wallpaper.getStatus().name());
            stmt.setString(9, wallpaper.getDeviceType().name());
            stmt.setString(10, wallpaper.getContentRating().name());
            stmt.setLong(11, wallpaper.getUploaderId());
            stmt.setTimestamp(12, Timestamp.valueOf(wallpaper.getUpdatedAt()));
            stmt.setLong(13, wallpaper.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating wallpaper: {}", wallpaper, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM wallpapers WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting wallpaper by id: {}", id, e);
        }
    }

    @Override
    public List<Wallpaper> findByStatus(WallpaperStatus status) {
        List<Wallpaper> wallpapers = new ArrayList<>();
        String sql = "SELECT * FROM wallpapers WHERE status = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wallpapers.add(mapRowToWallpaper(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding wallpapers by status: {}", status, e);
        }
        return wallpapers;
    }

    @Override
    public List<Wallpaper> findByUploaderId(Long uploaderId) {
        List<Wallpaper> wallpapers = new ArrayList<>();
        String sql = "SELECT * FROM wallpapers WHERE uploader_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, uploaderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wallpapers.add(mapRowToWallpaper(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding wallpapers by uploader id: {}", uploaderId, e);
        }
        return wallpapers;
    }

    private Wallpaper mapRowToWallpaper(ResultSet rs) throws SQLException {
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setId(rs.getLong("id"));
        wallpaper.setTitle(rs.getString("title"));
        wallpaper.setDescription(rs.getString("description"));
        // ImageMetadata would be a JSON or separate columns, assuming JSON for now
        // wallpaper.setMetadata(...);
        wallpaper.setThumbnailUrl(rs.getString("thumbnail_url"));
        wallpaper.setMediumUrl(rs.getString("medium_url"));
        wallpaper.setFullUrl(rs.getString("full_url"));
        wallpaper.setWatermarkUrl(rs.getString("watermark_url"));
        wallpaper.setPrice(rs.getBigDecimal("price"));
        wallpaper.setStatus(WallpaperStatus.valueOf(rs.getString("status")));
        wallpaper.setDeviceType(DeviceType.valueOf(rs.getString("device_type")));
        wallpaper.setContentRating(ContentRating.valueOf(rs.getString("content_rating")));
        wallpaper.setViewCount(rs.getInt("view_count"));
        wallpaper.setDownloadCount(rs.getInt("download_count"));
        wallpaper.setPurchaseCount(rs.getInt("purchase_count"));
        wallpaper.setUploaderId(rs.getLong("uploader_id"));
        wallpaper.setReviewerId(rs.getLong("reviewer_id"));
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