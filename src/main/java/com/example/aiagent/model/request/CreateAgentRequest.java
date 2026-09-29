package com.example.aiagent.model.request;

import com.example.aiagent.model.dto.AgentDTO;
import lombok.Data;

import java.util.List;

/**
 * 创建智能体请求
 */
@Data
public class CreateAgentRequest {
    private String name;
    private String description;
    private String systemPrompt;
    private String model;
    private List<String> allowedTools;
    private List<String> allowedKbs;
    private AgentDTO.ChatOptions chatOptions;
}
