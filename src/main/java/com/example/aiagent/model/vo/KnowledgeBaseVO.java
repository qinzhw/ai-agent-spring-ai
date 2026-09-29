package com.example.aiagent.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 知识库视图对象
 */
@Data
@Builder
public class KnowledgeBaseVO {
    private String id;
    private String name;
    private String description;
}
