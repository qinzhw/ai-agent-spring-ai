package com.example.aiagent.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文档编排服务：上传解析 + 嵌入分块 + 级联删除
 */
public interface DocumentFacadeService {

    /**
     * 上传文档：保存文件 → 解析 → 嵌入 → 分块入库
     *
     * @param kbId 知识库 ID
     * @param file 上传的文件
     * @return 文档 ID
     */
    String uploadDocument(String kbId, MultipartFile file);

    /**
     * 删除文档：删文件 → 删 chunk → 删 document 记录
     *
     * @param documentId 文档 ID
     */
    void deleteDocument(String documentId);
}
