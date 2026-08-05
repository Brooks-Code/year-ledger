package com.brooks.mall.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegisterRequest {

    /**
     * 登录账号 (对应 userid)
     */
    @NotBlank(message = "登录账号不能为空")
    @Size(min = 3, max = 32, message = "账号长度必须在3-32位之间")
    private String userid;

    /**
     * 真实姓名 (对应 real_name)
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50位")
    private String realName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    //@Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 确认密码 (仅用于前端二次校验，不存入数据库)
     */
    @NotBlank(message = "请再次输入密码")
    private String confirmPassword;

    /**
     * 身份证号 (对应 id_card)
     * 支持15位、18位及末位X
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    private String idCard;

    /**
     * 手机号 (对应 mobile)
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 邮箱 (对应 email)
     */
    @Pattern(
            regexp = "^([a-zA-Z0-9_\\-]+@[a-zA-Z0-9_\\-]+(\\.[a-zA-Z0-9_\\-]+)+)$|^$",
            message = "邮箱格式不正确"
    )
    private String email;

    /**
     * 用户头像URL (对应 avatar)
     * 选填，如果前端已上传图片则传入URL，否则使用默认头像
     */
    @Size(max = 255, message = "头像地址过长")
    private String avatar;
}