package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 标签实体
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_label")
public class LabelEntity extends BaseEntity {
    private String name;
    // 使用次数
    private Integer useCount;
}

