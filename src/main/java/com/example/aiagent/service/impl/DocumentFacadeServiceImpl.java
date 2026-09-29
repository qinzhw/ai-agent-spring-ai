package com.example.aiagent.service.impl;

import com.example.aiagent.model.entity.Chunk;
import com.example.aiagent.model.entity.Document;
import com.example.aiagent.service.ChunkService;
import com.example.aiagent.service.DocumentFacadeService;
import com.example.aiagent.service.DocumentService;
import com.example.aiagent.service.DocumentStorageService;
import com.example.aiagent.service.MarkdownParserService;
import com.example.aiagent.service.RagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * 文档编排服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentFacadeServiceImpl implements DocumentFacadeService {

    private final DocumentService documentService;
    private final DocumentStorageService documentStorageService;
    private final MarkdownParserService markdownParserService;
    private final RagService ragService;
    private final ChunkService chunkService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String uploadDocument(String kbId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("上传的文件为空");
            }

            String originalFilename = file.getOriginalFilename();
            String filetype = getFileType(originalFilename);
            long fileSize = file.getSize();

            // 创建文档记录
            Document document = new Document();
            document.setKbId(kbId);
            document.setFilename(originalFilename);
            document.setFiletype(filetype);
            document.setSize(fileSize);
            documentService.save(document);

            String documentId = document.getId();

            // 保存文件
            String filePath = documentStorageService.saveFile(kbId, documentId, file);

            // 更新 metadata 保存文件路径
            document.setMetadata(objectMapper.writeValueAsString(Map.of("filePath", filePath)));
            documentService.updateById(document);

            log.info("文档上传成功: kbId={}, documentId={}, filename={}", kbId, documentId, originalFilename);

            // 按 filetype 分发处理
            if ("md".equalsIgnoreCase(filetype) || "markdown".equalsIgnoreCase(filetype)) {
                processMarkdownDocument(kbId, documentId, filePath);
            } else {
                log.warn("暂不支持的文件类型: {}，跳过分块处理", filetype);
            }

            return documentId;
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new RuntimeException("文件保存失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDocument(String documentId) {
        Document document = documentService.getById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在: " + documentId);
        }

        // 删除文件
        try {
            String metadata = document.getMetadata();
            if (metadata != null) {
                Map<?, ?> meta = objectMapper.readValue(metadata, Map.class);
                String filePath = (String) meta.get("filePath");
                if (filePath != null) {
                    documentStorageService.deleteFile(filePath);
                }
            }
        } catch (Exception e) {
            log.warn("删除文件失败，继续删除数据库记录: documentId={}, error={}", documentId, e.getMessage());
        }

        // 删除 chunk（按 docId）
        chunkService.lambdaUpdate().eq(Chunk::getDocId, documentId).remove();

        // 删除 document 记录
        documentService.removeById(documentId);
        log.info("文档删除成功: documentId={}", documentId);
    }

    /**
     * 处理 Markdown 文档：解析章节 → 逐章嵌入 → 创建 Chunk 入库
     */
    private void processMarkdownDocument(String kbId, String documentId, String filePath) {
        try {
            log.info("开始处理 Markdown 文档: kbId={}, documentId={}, filePath={}", kbId, documentId, filePath);

            Path path = documentStorageService.getFilePath(filePath);
            try (InputStream inputStream = Files.newInputStream(path)) {
                List<MarkdownParserService.MarkdownSection> sections = markdownParserService.parseMarkdown(inputStream);

                if (sections.isEmpty()) {
                    log.warn("Markdown 文档解析后没有找到任何章节: documentId={}", documentId);
                    return;
                }

                int chunkCount = 0;
                for (MarkdownParserService.MarkdownSection section : sections) {
                    String title = section.getTitle();
                    String content = section.getContent();

                    if (title == null || title.trim().isEmpty()) {
                        continue;
                    }

                    // 对标题进行嵌入
                    float[] embedding = ragService.embed(title);

                    // 创建 Chunk
                    Chunk chunk = new Chunk();
                    chunk.setKbId(kbId);
                    chunk.setDocId(documentId);
                    chunk.setContent(content != null ? content : "");
                    chunk.setMetadata(objectMapper.writeValueAsString(Map.of("title", title)));
                    chunk.setEmbedding(embedding);
                    chunkService.save(chunk);

                    chunkCount++;
                    log.debug("创建 chunk 成功: title={}, chunkId={}", title, chunk.getId());
                }
                log.info("Markdown 文档处理完成: documentId={}, 共生成 {} 个 chunks", documentId, chunkCount);
            }
        } catch (Exception e) {
            log.error("处理 Markdown 文档失败: documentId={}", documentId, e);
        }
    }

    private String getFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
