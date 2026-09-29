package com.example.aiagent.model.request;

import lombok.Data;

/**
 * 创建会话请求
 */
@Data
public class CreateChatSessionRequest {
    private String agentId;
    private String title;
}
