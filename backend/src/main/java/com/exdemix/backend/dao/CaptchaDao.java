// CaptchaDao.java
package com.exdemix.backend.dao;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CaptchaDao {
    void saveCaptcha(String phone, String captcha, String captchaType, String ipAddress, LocalDateTime expireTime);
    Optional<String> getValidCaptcha(String phone, String captchaType);
    void markCaptchaAsUsed(String phone, String captcha, String captchaType);
    void deleteExpiredCaptchas();
    int getRecentCaptchaCount(String phone, LocalDateTime since);
}
