-- 用户笔记交互表
CREATE TABLE y_user_note_interaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    interaction_type TINYINT NOT NULL DEFAULT 0 COMMENT '交互类型位图: 1-点赞 2-收藏 4-分享(预留) 8-举报(预留)',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    
    -- 唯一索引：确保每个用户对每个笔记只有一条记录
    UNIQUE KEY uk_user_note (user_id, note_id),
    
    -- 复合索引：优化根据笔记ID和交互类型查询
    KEY idx_note_interaction (note_id, interaction_type),
    
    -- 复合索引：优化根据用户ID和交互类型查询
    KEY idx_user_interaction (user_id, interaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户笔记交互表';