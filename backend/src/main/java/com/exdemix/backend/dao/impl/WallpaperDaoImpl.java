package com.exdemix.backend.dao.impl;

import com.exdemix.backend.dao.WallpaperDao;
import com.exdemix.backend.entity.wallpaper.ContentRating;
import com.exdemix.backend.entity.wallpaper.DeviceType;
import com.exdemix.backend.entity.wallpaper.Wallpaper;
import com.exdemix.backend.entity.wallpaper.WallpaperStatus;
import com.exdemix.backend.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return wallpapers;
    }

    @Override
    public Wallpaper save(Wallpaper entity) {
        String sql = "INSERT INTO wallpapers (title, description, original_filename, thumbnail_url, medium_url, full_url, watermark_url, price, status, device_type, rating, uploader_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getDescription());
            stmt.setString(3, entity.getOriginalFilename());
            stmt.setString(4, entity.getThumbnailUrl());
            stmt.setString(5, entity.getMediumUrl());
            stmt.setString(6, entity.getFullUrl());
            stmt.setString(7, entity.getWatermarkUrl());
            stmt.setBigDecimal(8, entity.getPrice());
            stmt.setString(9, entity.getStatus().name());
            stmt.setString(10, entity.getDeviceType().name());
            stmt.setString(11, entity.getRating().name());
            stmt.setLong(12, entity.getUploaderId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void update(Wallpaper entity) {
        String sql = "UPDATE wallpapers SET title = ?, description = ?, original_filename = ?, thumbnail_url = ?, medium_url = ?, full_url = ?, watermark_url = ?, price = ?, status = ?, device_type = ?, rating = ?, uploader_id = ?, reviewer_id = ?, review_date = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getDescription());
            stmt.setString(3, entity.getOriginalFilename());
            stmt.setString(4, entity.getThumbnailUrl());
            stmt.setString(5, entity.getMediumUrl());
            stmt.setString(6, entity.getFullUrl());
            stmt.setString(7, entity.getWatermarkUrl());
            stmt.setBigDecimal(8, entity.getPrice());
            stmt.setString(9, entity.getStatus().name());
            stmt.setString(10, entity.getDeviceType().name());
            stmt.setString(11, entity.getRating().name());
            stmt.setLong(12, entity.getUploaderId());
            stmt.setLong(13, entity.getReviewerId());
            stmt.setTimestamp(14, entity.getReviewDate() != null ? Timestamp.valueOf(entity.getReviewDate()) : null);
            stmt.setLong(15, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public List<Wallpaper> findByStatus(WallpaperStatus status) {
        // Implementation for findByStatus
        return new ArrayList<>();
    }

    @Override
    public List<Wallpaper> findByUploaderId(Long uploaderId) {
        // Implementation for findByUploaderId
        return new ArrayList<>();
    }

    private Wallpaper mapRowToWallpaper(ResultSet rs) throws SQLException {
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setId(rs.getLong("id"));
        wallpaper.setTitle(rs.getString("title"));
        wallpaper.setDescription(rs.getString("description"));
        wallpaper.setOriginalFilename(rs.getString("original_filename"));
        // ImageMetadata would be a JSON or separate columns, assuming JSON for now
        // wallpaper.setMetadata(...);
        wallpaper.setThumbnailUrl(rs.getString("thumbnail_url"));
        wallpaper.setMediumUrl(rs.getString("medium_url"));
        wallpaper.setFullUrl(rs.getString("full_url"));
        wallpaper.setWatermarkUrl(rs.getString("watermark_url"));
        wallpaper.setPrice(rs.getBigDecimal("price"));
        wallpaper.setStatus(WallpaperStatus.valueOf(rs.getString("status")));
        wallpaper.setDeviceType(DeviceType.valueOf(rs.getString("device_type")));
        wallpaper.setRating(ContentRating.valueOf(rs.getString("rating")));
        wallpaper.setViewCount(rs.getInt("view_count"));
        wallpaper.setDownloadCount(rs.getInt("download_count"));
        wallpaper.setPurchaseCount(rs.getInt("purchase_count"));
        wallpaper.setUploaderId(rs.getLong("uploader_id"));
        wallpaper.setReviewerId(rs.getLong("reviewer_id"));
        if (rs.getTimestamp("review_date") != null) {
            wallpaper.setReviewDate(rs.getTimestamp("review_date").toLocalDateTime());
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