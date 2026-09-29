package com.example.aiagent.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * 创建知识库响应
 */
@Data
@Builder
public class CreateKnowledgeBaseResponse {
    private String knowledgeBaseId;
}
