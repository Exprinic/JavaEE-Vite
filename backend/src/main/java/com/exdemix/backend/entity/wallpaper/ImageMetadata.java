package com.exdemix.backend.entity.wallpaper;

/**
 * 壁纸元数据
 * 分离关注点，避免壁纸类过于臃肿
 */
public class ImageMetadata {
    private Long id;
    private Long wallpaperId;
    private Integer width;
    private Integer height;
    private String format;          // JPEG, PNG, WEBP等
    private Long fileSize;          // 文件大小（字节）
    private String colorPalette;    // 主色调（JSON数组）
    private Double aspectRatio;     // 宽高比
    private Boolean hasTransparency; // 是否有透明度
}