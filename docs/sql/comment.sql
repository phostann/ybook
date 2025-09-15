-- 评论表创建脚本（优化版本）
CREATE TABLE y_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    root_comment_id BIGINT DEFAULT NULL COMMENT '根评论ID，顶级评论为NULL',
    reply_to_comment_id BIGINT DEFAULT NULL COMMENT '回复的评论ID，顶级评论为NULL',
    content TEXT NOT NULL COMMENT '评论内容',
    comment_level INT DEFAULT 0 COMMENT '评论层级深度：0-顶级，1,2,3...-各级回复',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    reply_count INT DEFAULT 0 COMMENT '回复数',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除，0-否，1-是',
    ip_location VARCHAR(100) DEFAULT NULL COMMENT 'IP归属地',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    
    -- 索引优化
    KEY idx_note_root_level_time (note_id, root_comment_id, is_deleted, create_time DESC) COMMENT '笔记根评论查询索引',
    KEY idx_root_id_time (root_comment_id, is_deleted, create_time ASC) COMMENT '根评论回复查询索引', 
    KEY idx_user_id_deleted (user_id, is_deleted) COMMENT '用户评论查询索引',
    KEY idx_reply_to_id (reply_to_comment_id, is_deleted) COMMENT '回复关系查询索引',
    KEY idx_create_time (create_time DESC) COMMENT '时间排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表（支持多层嵌套和分页）';

-- 如果已存在旧表，需要先备份数据再删除重建
-- 或者使用ALTER语句添加新字段：
-- ALTER TABLE y_comment ADD COLUMN root_comment_id BIGINT DEFAULT NULL COMMENT '根评论ID' AFTER user_id;
-- ALTER TABLE y_comment ADD COLUMN reply_to_comment_id BIGINT DEFAULT NULL COMMENT '回复的评论ID' AFTER root_comment_id;  
-- ALTER TABLE y_comment ADD COLUMN comment_level INT DEFAULT 0 COMMENT '评论层级深度' AFTER reply_to_comment_id;
-- ALTER TABLE y_comment DROP COLUMN parent_id; -- 删除旧的parent_id字段