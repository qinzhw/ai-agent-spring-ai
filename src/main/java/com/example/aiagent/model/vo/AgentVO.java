package com.example.aiagent.model.vo;

import com.example.aiagent.model.dto.AgentDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 智能体视图对象
 * 只暴露前端需要的字段，不暴露审计字段
 */
@Data
@Builder
public class AgentVO {
    private String id;
    private String name;
    private String description;
    private String systemPrompt;
    private String model;
    private List<String> allowedTools;
    private List<String> allowedKbs;
    private AgentDTO.ChatOptions chatOptions;
}
