-- 用户关注关系表
CREATE TABLE y_user_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID（发起关注的用户）',
    following_id BIGINT NOT NULL COMMENT '被关注者ID（被关注的用户）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '关注状态: 1-关注 0-取消关注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    
    -- 唯一索引：确保每对用户只有一条关注记录
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    
    -- 复合索引：优化根据被关注者ID和状态查询粉丝列表
    KEY idx_following_status (following_id, status),
    
    -- 复合索引：优化根据关注者ID和状态查询关注列表
    KEY idx_follower_status (follower_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注关系表';