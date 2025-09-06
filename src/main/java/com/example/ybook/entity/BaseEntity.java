package com.example.ybook.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 柒
 * @since 2025-09-03 21:55:51
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4973028070959380105L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
