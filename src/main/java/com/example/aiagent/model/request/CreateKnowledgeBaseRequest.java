package com.example.aiagent.model.request;

import lombok.Data;

/**
 * 创建知识库请求
 */
@Data
public class CreateKnowledgeBaseRequest {
    private String name;
    private String description;
}
