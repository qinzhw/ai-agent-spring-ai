package com.example.aiagent.converter;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.aiagent.model.dto.AgentDTO;
import com.example.aiagent.model.entity.Agent;
import com.example.aiagent.model.request.CreateAgentRequest;
import com.example.aiagent.model.request.UpdateAgentRequest;
import com.example.aiagent.model.vo.AgentVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 智能体转换器
 * 处理 Entity ↔ DTO ↔ VO 之间的转换，包括 JSON 字段的序列化/反序列化
 */
@Component
@AllArgsConstructor
public class AgentConverter {

    public Agent toEntity(AgentDTO dto) {
        Assert.notNull(dto, "AgentDTO cannot be null");

        Agent agent = new Agent();
        agent.setId(dto.getId());
        agent.setName(dto.getName());
        agent.setDescription(dto.getDescription());
        agent.setSystemPrompt(dto.getSystemPrompt());
        agent.setModel(dto.getModel());
        agent.setAllowedTools(dto.getAllowedTools() != null ? JSONUtil.toJsonStr(dto.getAllowedTools()) : null);
        agent.setAllowedKbs(dto.getAllowedKbs() != null ? JSONUtil.toJsonStr(dto.getAllowedKbs()) : null);
        agent.setChatOptions(dto.getChatOptions() != null ? JSONUtil.toJsonStr(dto.getChatOptions()) : null);
        agent.setCreatedAt(dto.getCreatedAt());
        agent.setUpdatedAt(dto.getUpdatedAt());
        return agent;
    }

    public AgentDTO toDTO(Agent agent) {
        Assert.notNull(agent, "Agent cannot be null");

        return AgentDTO.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .systemPrompt(agent.getSystemPrompt())
                .model(agent.getModel())
                .allowedTools(parseStringList(agent.getAllowedTools()))
                .allowedKbs(parseStringList(agent.getAllowedKbs()))
                .chatOptions(parseChatOptions(agent.getChatOptions()))
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }

    public AgentVO toVO(AgentDTO dto) {
        return AgentVO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .systemPrompt(dto.getSystemPrompt())
                .model(dto.getModel())
                .allowedTools(dto.getAllowedTools())
                .allowedKbs(dto.getAllowedKbs())
                .chatOptions(dto.getChatOptions())
                .build();
    }

    public AgentVO toVO(Agent agent) {
        return toVO(toDTO(agent));
    }

    public AgentDTO toDTO(CreateAgentRequest request) {
        Assert.notNull(request, "CreateAgentRequest cannot be null");

        return AgentDTO.builder()
                .name(request.getName())
                .description(request.getDescription())
                .systemPrompt(request.getSystemPrompt())
                .model(request.getModel())
                .allowedTools(request.getAllowedTools())
                .allowedKbs(request.getAllowedKbs())
                .chatOptions(request.getChatOptions())
                .build();
    }

    public void updateDTOFromRequest(AgentDTO dto, UpdateAgentRequest request) {
        Assert.notNull(dto, "AgentDTO cannot be null");
        Assert.notNull(request, "UpdateAgentRequest cannot be null");

        if (request.getName() != null) dto.setName(request.getName());
        if (request.getDescription() != null) dto.setDescription(request.getDescription());
        if (request.getSystemPrompt() != null) dto.setSystemPrompt(request.getSystemPrompt());
        if (request.getModel() != null) dto.setModel(request.getModel());
        if (request.getAllowedTools() != null) dto.setAllowedTools(request.getAllowedTools());
        if (request.getAllowedKbs() != null) dto.setAllowedKbs(request.getAllowedKbs());
        if (request.getChatOptions() != null) dto.setChatOptions(request.getChatOptions());
    }

    // ========================= 内部工具方法 =========================

    private java.util.List<String> parseStringList(String json) {
        if (!StringUtils.hasLength(json)) return java.util.Collections.emptyList();
        return JSONUtil.toList(json, String.class);
    }

    private AgentDTO.ChatOptions parseChatOptions(String json) {
        if (!StringUtils.hasLength(json)) return AgentDTO.ChatOptions.defaultOptions();
        try {
            return JSONUtil.toBean(json, AgentDTO.ChatOptions.class);
        } catch (Exception e) {
            return AgentDTO.ChatOptions.defaultOptions();
        }
    }
}
