package com.example.aiagent.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * 创建智能体响应
 */
@Data
@Builder
public class CreateAgentResponse {
    private String agentId;
}
