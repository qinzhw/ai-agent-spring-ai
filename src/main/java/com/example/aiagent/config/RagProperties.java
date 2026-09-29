package com.example.aiagent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RAG / Ollama 嵌入模型配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ollama")
public class RagProperties {

    /**
     * Ollama 服务地址
     */
    private String baseUrl = "http://localhost:11434";

    /**
     * 嵌入模型名称
     */
    private String embeddingModel = "bge-m3";

    /**
     * 相似度检索返回条数
     */
    private int similarityTopK = 3;

    /**
     * 文档存储根目录
     */
    private String documentStorageBasePath = "./data/documents";
}
