// PasswordUtil.java - 密码工具类
package com.exdemix.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class PasswordUtil {
    
    private static final SecureRandom random = new SecureRandom();
    private static final int BCRYPT_COST = 12;
    
    /**
     * 生成随机盐值
     */
    public String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 哈希密码（使用BCrypt）
     */
    public String hashPassword(String password, String salt) {
        try {
            // 将密码和盐值组合
            String passwordWithSalt = password + salt;
            return BCrypt.hashpw(passwordWithSalt, BCrypt.gensalt(BCRYPT_COST));
        } catch (Exception e) {
            log.error("Error hashing password", e);
            throw new RuntimeException("密码加密失败");
        }
    }
    
    /**
     * 验证密码
     */
    public boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        try {
            String passwordWithSalt = inputPassword + salt;
            return BCrypt.checkpw(passwordWithSalt, storedHash);
        } catch (Exception e) {
            log.error("Error verifying password", e);
            return false;
        }
    }
    
    /**
     * 检查密码强度
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        // 至少包含三种类型的字符
        int typeCount = 0;
        if (hasUpper) typeCount++;
        if (hasLower) typeCount++;
        if (hasDigit) typeCount++;
        if (hasSpecial) typeCount++;
        
        return typeCount >= 3;
    }
}