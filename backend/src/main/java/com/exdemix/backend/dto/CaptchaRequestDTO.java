package com.exdemix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CaptchaRequestDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;
    @NotBlank(message = "密码不能为空")
    @Size(min = 7, max = 20, message = "密码长度7-20位")
    @Pattern(regexp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*\\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\da-zA-Z])|(?=.*[a-z])(?=.*\\d)(?=.*[^\\da-zA-Z])|(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z])).{7,}$", message = "密码必须包含大写字母、小写字母、数字和特殊字符中的至少三种")
    private String password;
}
