package com.example.aiagent.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * 创建会话响应
 */
@Data
@Builder
public class CreateChatSessionResponse {
    private String chatSessionId;
}
