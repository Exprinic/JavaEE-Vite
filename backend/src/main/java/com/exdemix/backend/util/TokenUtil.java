// TokenUtil.java - 令牌工具类
package com.exdemix.backend.util;

import com.exdemix.backend.entity.user.User;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenUtil {
    
    private static final String SECRET_KEY = "YourSuperSecretKeyForJWTTokenGenerationThatShouldBeAtLeast32BytesLong";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private static final long ACCESS_TOKEN_EXPIRE_HOURS = 24;
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 30;
    
    private final Gson gson = GsonUtil.getGson();
    
    /**
     * 生成访问令牌
     */
    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRE_HOURS * 60 * 60 * 1000);
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRE_DAYS * 24 * 60 * 60 * 1000);
    }
    
    private String generateToken(User user, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        
        // 用户信息
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("nickname", user.getNickname());
        claims.put("phone", user.getPhone());
        claims.put("userType", user.getUserType().name());
        
        // 权限信息
        claims.put("roles", user.getRoles());
        claims.put("permissions", user.getAllPermissions());
        
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        
        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(KEY)
                .compact();
    }
    
    /**
     * 解析令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new RuntimeException("令牌已过期");
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            throw new RuntimeException("令牌无效");
        }
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取令牌中的用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * 获取令牌过期时间
     */
    public LocalDateTime getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    /**
     * 获取令牌中的权限信息
     */
    @SuppressWarnings("unchecked")
    public java.util.Set<String> getPermissionsFromToken(String token) {
        Claims claims = parseToken(token);
        Object permissionsObj = claims.get("permissions");
        
        if (permissionsObj instanceof java.util.List) {
            return new java.util.HashSet<>((java.util.List<String>) permissionsObj);
        } else if (permissionsObj instanceof String) {
            // 如果是JSON字符串，解析它
            return gson.fromJson((String) permissionsObj, java.util.HashSet.class);
        }
        
        return new java.util.HashSet<>();
    }
}