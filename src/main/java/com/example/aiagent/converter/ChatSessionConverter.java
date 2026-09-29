package com.example.aiagent.converter;

import com.example.aiagent.model.dto.ChatSessionDTO;
import com.example.aiagent.model.entity.ChatSession;
import com.example.aiagent.model.vo.ChatSessionVO;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 聊天会话转换器
 */
@Component
public class ChatSessionConverter {

    public ChatSessionDTO toDTO(ChatSession entity) {
        Assert.notNull(entity, "ChatSession cannot be null");
        return ChatSessionDTO.builder()
                .id(entity.getId())
                .agentId(entity.getAgentId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ChatSessionVO toVO(ChatSessionDTO dto) {
        return ChatSessionVO.builder()
                .id(dto.getId())
                .agentId(dto.getAgentId())
                .title(dto.getTitle())
                .build();
    }

    public ChatSessionVO toVO(ChatSession entity) {
        return toVO(toDTO(entity));
    }
}
