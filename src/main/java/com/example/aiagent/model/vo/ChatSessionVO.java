package com.example.aiagent.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 聊天会话视图对象
 */
@Data
@Builder
public class ChatSessionVO {
    private String id;
    private String agentId;
    private String title;
}
