package com.example.aiagent.converter;

import com.example.aiagent.model.dto.KnowledgeBaseDTO;
import com.example.aiagent.model.entity.KnowledgeBase;
import com.example.aiagent.model.vo.KnowledgeBaseVO;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 知识库转换器
 */
@Component
public class KnowledgeBaseConverter {

    public KnowledgeBaseDTO toDTO(KnowledgeBase entity) {
        Assert.notNull(entity, "KnowledgeBase cannot be null");
        return KnowledgeBaseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public KnowledgeBaseVO toVO(KnowledgeBaseDTO dto) {
        return KnowledgeBaseVO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public KnowledgeBaseVO toVO(KnowledgeBase entity) {
        return toVO(toDTO(entity));
    }
}
