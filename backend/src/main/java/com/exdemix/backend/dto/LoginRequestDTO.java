package com.exdemix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    // 密码格式要求：12位，包含大小写字母、数字、特殊字符
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$", message = "密码格式不正确")
    private String password;
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度4-6位")
    private String captcha;

    private boolean rememberMe =  false; // 是否记住我
}
