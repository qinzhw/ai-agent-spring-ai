package com.example.aiagent.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 聊天消息视图对象
 */
@Data
@Builder
public class ChatMessageVO {
    private String id;
    private String sessionId;
    private String role;
    private String content;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
