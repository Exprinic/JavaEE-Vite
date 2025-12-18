package com.exdemix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * 登录请求 DTO
 */
@Data
public class LoginRequestDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    // 密码格式要求：12位，包含大小写字母、数字、特殊字符
    @NotBlank(message = "密码不能为空")
    @Size(min = 7, max = 20, message = "密码长度7-20位")
    @Pattern(regexp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*\\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\da-zA-Z])|(?=.*[a-z])(?=.*\\d)(?=.*[^\\da-zA-Z])|(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z])).{7,}$", message = "密码必须包含大写字母、小写字母、数字和特殊字符中的至少三种")
    private String password;
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "\\d{6}", message = "验证码必须是6位数字")
    private String captcha;

    private boolean rememberMe =  false; // 是否记住我
}
