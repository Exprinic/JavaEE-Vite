package com.exdemix.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CaptchaResponseVO {
    private String captcha;
}
