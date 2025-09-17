# 用户聊天功能实施方案（基于 YBook 现有架构）

面向现有后端（Spring Boot 3.5 + MyBatis‑Plus + JWT + MinIO + OpenAPI），规划一对一私聊的实时通信能力；本文为技术选型与落地计划，不对现有代码做任何修改。

## 1. 当前架构要点
- 后端：Spring Boot 3.5、Java 21、REST 风格、统一 `ApiResult` 响应。
- 安全：JWT 无状态认证（`JwtAuthenticationFilter`）、全局 CORS；需为 WebSocket 握手端点放行。
- 数据：MySQL + MyBatis‑Plus，`BaseEntity` 自增主键 + 自动时间填充。
- 其他：MapStruct 转换、MinIO 文件服务（可复用发送图片/文件消息）、OpenAPI 文档。

## 2. 技术选型
- 实时通道：WebSocket + STOMP（spring-boot-starter-websocket）；单机使用 SimpleBroker，横向扩展可切换 RabbitMQ STOMP Relay 或 Redis Pub/Sub。
- 存储模型：MySQL 持久化会话与消息；后续可引入 Redis 做在线状态、未读计数与最近消息缓存。
- 认证方案：WS 握手阶段校验 JWT，建立 `Principal`，在消息通道做鉴权。
- 消息类型：TEXT / IMAGE / FILE（文件走现有 MinIO 上传，消息体记录 URL）。
- 分页策略：历史消息按会话 + 时间倒序，优先 Keyset（基于 `id` 或 `create_time` 游标）。

## 3. 数据模型（表结构）

### 3.1 会话表 `y_conversation`
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
    UNIQUE KEY uk_participants (participant_one, participant_two),
    KEY idx_last_message_time (last_message_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.2 消息表 `y_message`
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
    message_status VARCHAR(20) DEFAULT 'SENT', -- SENT/DELIVERED/READ
    delivered_time DATETIME,
    read_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    KEY idx_conversation_time (conversation_id, create_time),
    KEY idx_sender (sender_id),
    KEY idx_receiver_status (receiver_id, message_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.3 可扩展（群聊与回执）
- 群聊：`y_conversation_member(user_id, conversation_id, role, last_read_message_id)`。
- 精确回执：`y_message_receipt(message_id, user_id, status, time)`。

## 4. 后端分层与包结构
- `entity`/`mapper`：`ConversationEntity`, `MessageEntity` + 对应 Mapper；遵循 MyBatis‑Plus 风格。
- `service`/`impl`：`ChatService`（建会话、发消息、查历史、标记已读、统计未读）。
- `dto`/`vo`/`converter`：`ConversationCreateDTO`, `MessageSendDTO`, `ConversationVO`, `MessageVO`；用 MapStruct。
- `controller`：REST `ChatController`（会话列表、历史消息、已读上报、未读数）。
- `config`：`WebSocketConfig`（启用 STOMP、端点 `/ws`、应用前缀 `/app`、用户前缀 `/user`）。
- `security`：`JwtWebSocketHandshakeInterceptor` + `ChannelInterceptor`（校验 token、注入用户身份）。

## 5. WebSocket 路由与约定
- 端点：`/ws`（可选 SockJS）；允许跨域，与现有 CORS 对齐。
- 发送：`/app/chat.send`，消息体含 `conversationId | targetUserId`, `messageType`, `content | fileUrl`, `clientMsgId`。
- 订阅：
  - 私聊会话：`/topic/conversations/{conversationId}`（双方订阅，用于多端同步）。
  - 个人队列：`/user/queue/notifications`（系统通知、离线补投）。
- 回执与去重：服务端生成 `id`、`serverTime`，响应 ACK（包含 `clientMsgId`）用于前端去重。
- 状态变更：DELIVERED/READ 通过 WS 或 REST 批量上报。

## 6. REST API 设计
- `GET /api/chat/conversations`：我的会话列表（含最后一条与未读数）。
- `POST /api/chat/conversations`：创建/获取一对一会话（传对方 `userId`，幂等）。
- `GET /api/chat/conversations/{id}/messages?cursor=&size=`：分页历史（Keyset）。
- `POST /api/chat/conversations/{id}/read`：已读上报（可带 `uptoMessageId`）。
- `GET /api/chat/unread/count`：总未读数。
- 文件发送：沿用 `POST /api/files/upload`，拿 `url` 后通过 WS 以 FILE/IMAGE 消息下发。

## 7. 认证与安全
- 握手：从 `Authorization: Bearer <token>` 或 `token` 查询参数读取 JWT，`JwtService` 校验后将 `Principal` 绑定到会话。
- 授权：发送与订阅时校验会话成员关系；仅允许查看/写入自己参与的会话。
- 安全配置：`SecurityConfig` 放行 `/ws/**` 握手；`/api/chat/**` 走认证链。
- 限流与防刷：发送频率与消息大小阈值；图片/文件类型白名单。

## 8. 在线状态与离线处理
- 单机：内存维护在线用户与订阅表；连接事件广播 presence。
- 扩展：接入 Redis
  - `online_users:set`、`user_sessions:{userId}`、`unread_count:{userId}`、`recent_messages:{conversationId}`。
- 离线：消息落库；上线后通过会话订阅或个人队列补投；REST 补拉兜底。

## 9. 可扩展性与部署
- 单实例起步：SimpleBroker 可落地。
- 多实例：启用 STOMP Relay（RabbitMQ）或改用自建分发（Redis Pub/Sub + `SimpMessagingTemplate`）。
- 索引与查询：确保 `(conversation_id, create_time)` 覆盖历史查询；未读数用累加/回写避免热点扫描。

## 10. 实施步骤与里程碑（不改动现有代码，先出方案与脚本）
1) 阶段1（0.5 天）：DDL 评审与落库草案（新增 `docs/sql/chat.sql`），评估索引与外键策略。
2) 阶段2（1 天）：定义实体/Mapper/DTO/VO/Converter 的接口与签名草图（仅文档）。
3) 阶段3（1 天）：WebSocket 架构与握手/通道拦截策略说明，路由与订阅约定文档化。
4) 阶段4（0.5 天）：消息去重与 ACK、已读/送达状态时序与接口约定。
5) 阶段5（0.5 天）：未读数与回写策略、关键 SQL 与分页策略说明。
6) 阶段6（可选 1 天）：在线状态/正在输入/多端同步；Redis 缓存方案评审。

## 11. 交付物
- 技术设计文档（本文件）与接口/数据结构清单。
- SQL 初稿：`docs/sql/chat.sql`（会话/消息表）；OpenAPI 变更点说明草案。
- 端到端用例：登录、建会话、发文本/图片、拉历史、已读上报与回执流程。
- 基础测试清单：WS 握手/鉴权、消息发送/订阅、REST 历史分页（文档级）。

## 12. 风险与注意事项
- 安全：订阅路径与会话成员校验必须严格；避免越权。
- 性能：大会话历史分页采用 Keyset；索引命中与 SQL 扫描评估。
- 可用性：网络抖动与断线重连；消息幂等与去重策略；离线补偿。
- 扩展：多实例时尽早确定 STOMP Relay 或 Redis 分发；消息广播一致性。

---
说明：本方案仅为实施计划与技术选型，不对现有代码进行改动；如需，我可继续输出 SQL 初稿与类/方法级接口清单供评审。

