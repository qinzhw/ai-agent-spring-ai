package com.example.aiagent.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.InputStream;
import java.util.List;

/**
 * Markdown 解析服务：按标题章节分割文档
 */
public interface MarkdownParserService {

    /**
     * 解析 Markdown 文件，提取标题和对应的内容
     *
     * @param inputStream Markdown 文件输入流
     * @return 标题和内容的列表
     */
    List<MarkdownSection> parseMarkdown(InputStream inputStream);

    /**
     * Markdown 章节数据类
     */
    @Data
    @AllArgsConstructor
    @ToString
    class MarkdownSection {
        private String title;
        private String content;
    }
}
