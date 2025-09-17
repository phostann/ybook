# 用户聊天功能实现计划

## 项目背景
基于现有YBook项目（Spring Boot 3.x + MyBatis Plus + JWT认证），新增用户间实时聊天功能。

## 技术选型

### 实时通信
- **WebSocket + STOMP协议** - Spring Boot原生支持，易于集成JWT认证
- 备选方案：Server-Sent Events (SSE) - 适合单向推送场景

### 数据存储
- **主存储**：MySQL - 持久化聊天记录
- **缓存层**：Redis - 在线用户状态、最近消息缓存  
- **文件存储**：MinIO - 复用现有文件存储系统

### 消息模型
**两表设计方案：**
- `y_conversation` - 会话表（记录参与者、会话元信息）
- `y_message` - 消息表（存储具体消息内容）

## 核心功能需求
1. **一对一私聊** - 用户之间直接聊天
2. **实时消息传输** - 消息即时送达
3. **消息类型支持** - 文本、图片、文件
4. **消息状态管理** - 已发送、已送达、已读
5. **历史消息** - 分页查询聊天记录
6. **在线状态** - 显示用户在线/离线状态

## 详细实施计划

### 阶段1: 数据模型设计 (1-2天)

#### 1.1 ConversationEntity - 会话实体
```java
@TableName("y_conversation")
public class ConversationEntity extends BaseEntity {
    private String conversationType; // "PRIVATE" - 一对一私聊
    private Long participantOne;     // 参与者1 ID
    private Long participantTwo;     // 参与者2 ID  
    private Long lastMessageId;      // 最后一条消息ID
    private LocalDateTime lastMessageTime; // 最后消息时间
    private Integer unreadCountOne;  // 参与者1未读数量
    private Integer unreadCountTwo;  // 参与者2未读数量
    private String lastMessagePreview; // 最后消息预览(50字符)
}
```

#### 1.2 MessageEntity - 消息实体
```java
@TableName("y_message") 
public class MessageEntity extends BaseEntity {
    private Long conversationId;    // 所属会话ID
    private Long senderId;          // 发送者ID
    private Long receiverId;        // 接收者ID
    private String messageType;     // "TEXT", "IMAGE", "FILE"
    private String content;         // 消息内容
    private String fileUrl;         // 文件URL(图片/文件消息)
    private String fileName;        // 文件名
    private Long fileSize;          // 文件大小
    private String messageStatus;   // "SENT", "DELIVERED", "READ"
    private LocalDateTime deliveredTime; // 送达时间
    private LocalDateTime readTime;      // 已读时间
}
```

#### 1.3 DTO/VO设计
- `ConversationCreateDTO` - 创建会话请求
- `MessageSendDTO` - 发送消息请求  
- `ConversationVO` - 会话视图对象
- `MessageVO` - 消息视图对象
- `OnlineUserVO` - 在线用户状态

### 阶段2: WebSocket集成 (2-3天)

#### 2.1 WebSocket配置
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // 配置STOMP端点和消息代理
    // 集成JWT认证拦截器
}
```

#### 2.2 消息路由设计
- `/app/chat.send` - 发送消息
- `/topic/user/{userId}` - 用户消息订阅
- `/topic/conversation/{conversationId}` - 会话消息订阅

#### 2.3 在线状态管理
- Redis存储在线用户列表: `online_users:set`
- 用户连接/断开时更新状态
- 定期心跳检测机制

### 阶段3: 核心API开发 (3-4天)

#### 3.1 ChatController - REST API
```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    // GET /conversations - 获取用户会话列表
    // POST /conversations - 创建新会话
    // GET /conversations/{id}/messages - 获取历史消息(分页)
    // PUT /messages/{id}/read - 标记消息已读
    // GET /online-users - 获取在线用户列表
}
```

#### 3.2 ChatService - 业务逻辑
- 会话创建和管理逻辑
- 消息发送和状态更新
- 未读数量统计
- 在线状态查询

#### 3.3 ChatMapper - 数据访问
- 复杂查询SQL优化
- 分页查询实现
- 批量状态更新

### 阶段4: 实时消息功能 (2-3天)

#### 4.1 WebSocket消息处理器
```java
@Controller
public class ChatWebSocketController {
    @MessageMapping("/chat.send")
    @SendToUser("/topic/messages")
    public void sendMessage(MessageSendDTO message, Principal principal);
    
    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event);
    
    @EventListener  
    public void handleWebSocketDisconnect(SessionDisconnectEvent event);
}
```

#### 4.2 消息状态更新
- 消息发送后自动标记为"SENT"
- 接收方在线时自动标记为"DELIVERED"  
- 用户读取消息时标记为"READ"

#### 4.3 多端同步
- 同一用户多设备登录时消息广播
- WebSocket会话管理

#### 4.4 离线消息处理
- 用户离线时消息持久化
- 用户上线时推送未读消息
- 批量消息状态更新

### 阶段5: 文件消息支持 (1-2天)

#### 5.1 集成MinIO文件上传
- 复用现有`FileUploadController`
- 扩展支持聊天文件上传端点
- 文件类型和大小限制

#### 5.2 图片消息处理
- 图片缩略图生成
- 支持图片预览和原图查看
- 图片压缩优化

#### 5.3 文件消息处理  
- 支持文档、音频、视频等文件
- 文件下载和预览功能
- 文件安全性检查

## 数据库设计

### 会话表 (y_conversation)
```sql
CREATE TABLE y_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    participant_one BIGINT NOT NULL,
    participant_two BIGINT NOT NULL,
    last_message_id BIGINT,
    last_message_time DATETIME,
    unread_count_one INT DEFAULT 0,
    unread_count_two INT DEFAULT 0,
    last_message_preview VARCHAR(100),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_participants (participant_one, participant_two),
    INDEX idx_last_message_time (last_message_time)
);
```

### 消息表 (y_message)
```sql
CREATE TABLE y_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL, 
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    content TEXT,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    message_status VARCHAR(20) DEFAULT 'SENT',
    delivered_time DATETIME,
    read_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_conversation_time (conversation_id, create_time),
    INDEX idx_sender (sender_id),
    INDEX idx_receiver_status (receiver_id, message_status)
);
```

## 技术实现要点

### WebSocket认证
```java
@Component
public class JwtWebSocketInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 从WebSocket握手时验证JWT token
        // 设置用户认证信息到Spring Security Context
    }
}
```

### Redis缓存设计
- `online_users` - 在线用户集合
- `user_sessions:{userId}` - 用户WebSocket会话ID
- `unread_count:{userId}` - 用户总未读数缓存
- `recent_messages:{conversationId}` - 最近消息缓存

### 性能优化策略
1. **消息分页** - 按时间倒序分页加载
2. **连接池** - WebSocket连接复用
3. **批量更新** - 消息状态批量更新SQL
4. **索引优化** - 合理设计数据库索引
5. **缓存策略** - Redis缓存热点数据

## 安全考虑
1. **输入验证** - 消息内容XSS防护
2. **文件安全** - 上传文件类型和大小限制  
3. **权限控制** - 只能查看自己参与的会话
4. **频率限制** - 消息发送频率限制
5. **敏感信息** - 避免敏感数据传输

## 测试计划
1. **单元测试** - Service层业务逻辑测试
2. **集成测试** - WebSocket连接和消息传输测试
3. **性能测试** - 并发用户和消息吞吐量测试
4. **安全测试** - 认证和权限控制测试

## 预计时间
- **总开发时间**: 9-14天
- **测试时间**: 2-3天
- **部署优化**: 1-2天

## 风险评估
1. **WebSocket连接稳定性** - 网络断开重连机制
2. **消息丢失风险** - 消息持久化和重试机制
3. **性能瓶颈** - 大量并发连接时的处理能力
4. **数据一致性** - 分布式环境下的消息状态同步

## 后续扩展
1. **群聊功能** - 多人会话支持
2. **消息撤回** - 限时消息撤回功能
3. **消息转发** - 消息转发和引用功能
4. **富文本消息** - 支持表情、链接预览等
5. **语音消息** - 音频消息录制和播放