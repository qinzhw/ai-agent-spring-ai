package com.example.aiagent.model.request;

import com.example.aiagent.model.dto.AgentDTO;
import lombok.Data;

import java.util.List;

/**
 * 更新智能体请求（所有字段可选，只更新非 null 字段）
 */
@Data
public class UpdateAgentRequest {
    private String name;
    private String description;
    private String systemPrompt;
    private String model;
    private List<String> allowedTools;
    private List<String> allowedKbs;
    private AgentDTO.ChatOptions chatOptions;
}
