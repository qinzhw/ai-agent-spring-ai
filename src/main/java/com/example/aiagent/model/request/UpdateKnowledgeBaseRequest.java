package com.example.aiagent.model.request;

import lombok.Data;

/**
 * 更新知识库请求（所有字段可选）
 */
@Data
public class UpdateKnowledgeBaseRequest {
    private String name;
    private String description;
}
