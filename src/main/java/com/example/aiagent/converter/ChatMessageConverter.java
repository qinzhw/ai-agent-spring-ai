package com.example.aiagent.converter;

import cn.hutool.json.JSONUtil;
import com.example.aiagent.model.dto.ChatMessageDTO;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.model.vo.ChatMessageVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

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

    public ChatMessageVO toVO(ChatMessageDTO dto) {
        return ChatMessageVO.builder()
                .id(dto.getId())
                .sessionId(dto.getSessionId())
                .role(dto.getRole())
                .content(dto.getContent())
                .metadata(dto.getMetadata())
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
}
