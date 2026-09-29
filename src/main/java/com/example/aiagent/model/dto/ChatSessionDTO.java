package com.example.aiagent.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话数据传输对象
 */
@Data
@Builder
public class ChatSessionDTO {
    private String id;
    private String agentId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
