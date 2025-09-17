-- 聊天功能数据库表设计
-- 遵循项目规范：y_前缀，snake_case命名

-- 1. 聊天室表 (支持私聊和群聊)
CREATE TABLE y_chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '聊天室ID',
    room_name VARCHAR(100) DEFAULT NULL COMMENT '聊天室名称(群聊时使用)',
    room_type ENUM('PRIVATE', 'GROUP') NOT NULL COMMENT '聊天室类型：PRIVATE-私聊，GROUP-群聊',
    room_avatar VARCHAR(500) DEFAULT NULL COMMENT '聊天室头像(群聊时使用)',
    room_description TEXT DEFAULT NULL COMMENT '聊天室描述(群聊时使用)',
    creator_id BIGINT NOT NULL COMMENT '创建者用户ID',
    last_message_id BIGINT DEFAULT NULL COMMENT '最后一条消息ID',
    last_message_time DATETIME DEFAULT NULL COMMENT '最后消息时间',
    member_count INT DEFAULT 0 COMMENT '成员数量',
    status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE' COMMENT '聊天室状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_room_type (room_type),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天室表';

-- 2. 聊天室成员表
CREATE TABLE y_chat_room_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成员关系ID',
    room_id BIGINT NOT NULL COMMENT '聊天室ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER' COMMENT '角色：OWNER-群主，ADMIN-管理员，MEMBER-普通成员',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '群昵称',
    mute_until DATETIME DEFAULT NULL COMMENT '禁言截止时间',
    join_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    last_read_time DATETIME DEFAULT NULL COMMENT '最后阅读时间',
    unread_count INT DEFAULT 0 COMMENT '未读消息数',
    status ENUM('ACTIVE', 'LEFT', 'KICKED') DEFAULT 'ACTIVE' COMMENT '成员状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_room_user (room_id, user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_last_read_time (last_read_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天室成员表';

-- 3. 消息表
CREATE TABLE y_chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    room_id BIGINT NOT NULL COMMENT '聊天室ID',
    sender_id BIGINT NOT NULL COMMENT '发送者用户ID',
    message_type ENUM('TEXT', 'IMAGE', 'FILE', 'VOICE', 'VIDEO', 'SYSTEM') NOT NULL COMMENT '消息类型',
    content TEXT NOT NULL COMMENT '消息内容',
    file_url VARCHAR(500) DEFAULT NULL COMMENT '文件URL(图片、文件、语音、视频)',
    file_name VARCHAR(200) DEFAULT NULL COMMENT '文件名称',
    file_size BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    reply_to_id BIGINT DEFAULT NULL COMMENT '回复的消息ID',
    sequence_id BIGINT NOT NULL COMMENT '消息序列号',
    read_count INT DEFAULT 0 COMMENT '已读人数',
    status ENUM('NORMAL', 'RECALLED', 'DELETED') DEFAULT 'NORMAL' COMMENT '消息状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_create_time (create_time),
    INDEX idx_sequence_id (sequence_id),
    INDEX idx_reply_to_id (reply_to_id),
    INDEX idx_room_sequence (room_id, sequence_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 4. 消息状态表 (已读状态)
CREATE TABLE y_chat_message_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '状态ID',
    message_id BIGINT NOT NULL COMMENT '消息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status ENUM('SENT', 'DELIVERED', 'READ') NOT NULL COMMENT '状态：SENT-已发送，DELIVERED-已送达，READ-已读',
    status_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_message_user (message_id, user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_status_time (status_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息状态表';

-- 5. 在线状态表 (用户在线状态)
CREATE TABLE y_user_online_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '状态ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status ENUM('ONLINE', 'OFFLINE', 'AWAY', 'BUSY') DEFAULT 'OFFLINE' COMMENT '在线状态',
    last_active_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    device_type VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    ip_address VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
    user_agent TEXT DEFAULT NULL COMMENT '用户代理',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_last_active_time (last_active_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户在线状态表';

-- 注释：不使用外键约束，通过应用层代码保证数据一致性
-- 在删除用户、聊天室等相关数据时，需要在Service层手动处理级联删除逻辑