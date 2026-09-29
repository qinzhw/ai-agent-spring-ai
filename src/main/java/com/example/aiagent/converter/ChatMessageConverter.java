package com.example.aiagent.converter;

import cn.hutool.json.JSONUtil;
import com.example.aiagent.model.dto.ChatMessageDTO;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.model.vo.ChatMessageVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 聊天消息转换器
 */
@Component
@AllArgsConstructor
public class ChatMessageConverter {

    public ChatMessage toEntity(ChatMessageDTO dto) {
        Assert.notNull(dto, "ChatMessageDTO cannot be null");
        ChatMessage entity = new ChatMessage();
        entity.setId(dto.getId());
        entity.setSessionId(dto.getSessionId());
        entity.setRole(dto.getRole());
        entity.setContent(dto.getContent());
        entity.setMetadata(dto.getMetadata() != null ? JSONUtil.toJsonStr(dto.getMetadata()) : null);
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    public ChatMessageDTO toDTO(ChatMessage entity) {
        Assert.notNull(entity, "ChatMessage cannot be null");
        return ChatMessageDTO.builder()
                .id(entity.getId())
                .sessionId(entity.getSessionId())
                .role(entity.getRole())
                .content(entity.getContent())
                .metadata(parseMetadata(entity.getMetadata()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /** 固定工具名称 —— 不在前端展示 */
    private static final Set<String> FIXED_TOOL_NAMES = Set.of("directAnswer", "terminate");

    public ChatMessageVO toVO(ChatMessageDTO dto) {
        Map<String, Object> metadata = dto.getMetadata();
        // 根据角色转换 metadata 结构以匹配前端期望
        if ("tool".equals(dto.getRole()) && metadata != null) {
            metadata = buildToolResponseMetadata(metadata, dto.getContent());
        } else if ("assistant".equals(dto.getRole()) && metadata != null) {
            metadata = filterFixedToolCalls(metadata);
        }
        return ChatMessageVO.builder()
                .id(dto.getId())
                .sessionId(dto.getSessionId())
                .role(dto.getRole())
                .content(dto.getContent())
                .metadata(metadata)
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public ChatMessageVO toVO(ChatMessage entity) {
        return toVO(toDTO(entity));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMetadata(String json) {
        if (!StringUtils.hasLength(json)) return null;
        try {
            return JSONUtil.toBean(json, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将 tool 消息的 metadata {toolName, toolCallId} 转换为前端期望的
     * {toolResponse: {id, name, responseData}} 结构
     */
    private Map<String, Object> buildToolResponseMetadata(Map<String, Object> rawMetadata, String content) {
        String toolName = String.valueOf(rawMetadata.getOrDefault("toolName", "unknown"));
        String toolCallId = String.valueOf(rawMetadata.getOrDefault("toolCallId", ""));
        return Map.of("toolResponse", Map.of(
                "id", toolCallId,
                "name", toolName,
                "responseData", content != null ? content : ""
        ));
    }

    /**
     * 过滤 assistant 消息中的固定工具调用（directAnswer / terminate），
     * 这些是内部工具，不应展示给用户
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> filterFixedToolCalls(Map<String, Object> metadata) {
        Object toolCallsObj = metadata.get("toolCalls");
        if (!(toolCallsObj instanceof List<?> toolCalls)) {
            return metadata;
        }
        List<Map<String, Object>> filtered = toolCalls.stream()
                .filter(tc -> {
                    if (tc instanceof Map<?, ?> map) {
                        return !FIXED_TOOL_NAMES.contains(String.valueOf(map.get("name")));
                    }
                    return true;
                })
                .map(tc -> (Map<String, Object>) tc)
                .toList();
        return Map.of("toolCalls", filtered);
    }
}
