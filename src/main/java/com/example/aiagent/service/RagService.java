package com.example.aiagent.service;

import java.util.List;

/**
 * RAG 服务：文本嵌入与相似度检索
 */
public interface RagService {

    /**
     * 对文本进行嵌入
     *
     * @param text 输入文本
     * @return 嵌入向量
     */
    float[] embed(String text);

    /**
     * 基于查询文本在指定知识库中执行相似度检索
     *
     * @param kbId  知识库 ID
     * @param query 查询文本
     * @return 匹配的文本片段列表
     */
    List<String> similaritySearch(String kbId, String query);
}
