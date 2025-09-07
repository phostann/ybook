package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 笔记标签关联实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_note_label")
public class NoteLabelEntity extends BaseEntity {
    
    /**
     * 笔记ID
     */
    private Long noteId;
    
    /**
     * 标签ID
     */
    private Long labelId;
}