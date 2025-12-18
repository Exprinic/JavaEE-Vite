package com.exdemix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求 DTO
 */
@Data
public class RegisterRequestDTO {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名格式错误，只能包含字母、数字、下划线和短横线，长度为4-16个字符")
    private String nickname;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;
    @NotBlank(message = "密码不能为空")
    @Size(min = 7, max = 20, message = "密码长度7-20位")
    @Pattern(regexp = "^((?=.*[a-z])(?=.*[A-Z])(?=.*\\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\da-zA-Z])|(?=.*[a-z])(?=.*\\d)(?=.*[^\\da-zA-Z])|(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z])).{7,}$", message = "密码必须包含大写字母、小写字母、数字和特殊字符中的至少三种")
    private String password;
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "\\d{6}", message = "验证码必须是6位数字")
    private String captcha;

    private String inviteCode;
}