package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * @author 柒
 * @since 2025-09-03 21:54:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_user")
public class UserEntity extends BaseEntity {
    private String username;
    // 密码，返回给前端时，不显示
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    // 头像，必填字段
    @NotBlank(message = "头像不能为空")
    private String avatar;
    // 昵称
    private String nickname;
    private String gender; // 0: unknown; 1: 男; 2: 女
    private String email;
    private String phone;
    private String status; // 0: disabled; 1: normal
}
