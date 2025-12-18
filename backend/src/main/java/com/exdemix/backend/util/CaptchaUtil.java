// CaptchaUtil.java - 验证码工具类
package com.exdemix.backend.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
public class CaptchaUtil {
    
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final String ALL_CHARS = NUMBERS + LETTERS;
    private static final Random random = new SecureRandom();
    
    /**
     * 生成数字验证码
     */
    public String generateNumericCaptcha(int length) {
        StringBuilder captcha = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            captcha.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return captcha.toString();
    }
    
    /**
     * 生成字母数字验证码
     */
    public String generateAlphanumericCaptcha(int length) {
        StringBuilder captcha = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            captcha.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        return captcha.toString();
    }
    
    /**
     * 生成验证码（默认数字）
     */
    public String generateCaptcha(int length) {
        return generateNumericCaptcha(length);
    }
    
    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String input, String stored, boolean caseSensitive) {
        if (input == null || stored == null) {
            return false;
        }
        
        if (caseSensitive) {
            return input.equals(stored);
        } else {
            return input.equalsIgnoreCase(stored);
        }
    }
    
    /**
     * 检查验证码是否过期
     */
    public boolean isCaptchaExpired(LocalDateTime generatedTime, int expireMinutes) {
        return generatedTime.plusMinutes(expireMinutes).isBefore(LocalDateTime.now());
    }
    
    /**
     * 生成图形验证码（返回Base64）
     */
    public String generateImageCaptchaBase64(String text, int width, int height) {
        // 在实际应用中，这里应该使用图形库生成验证码图片
        // 为了简化，这里返回空字符串，实际应返回Base64编码的图片
        log.info("Generated image captcha for text: {}", text);
        return ""; // 实际项目中应返回真正的Base64图片
    }
}
