package com.example.aiagent.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 聊天消息数据传输对象
 */
@Data
@Builder
public class ChatMessageDTO {
    private String id;
    private String sessionId;
    private String role;
    private String content;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
