package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.entity.Document;
import com.example.aiagent.service.DocumentFacadeService;
import com.example.aiagent.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档管理接口
 */
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "文档管理")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentFacadeService documentFacadeService;

    @PostMapping
    public BaseResponse<Document> create(@RequestBody Document document) {
        documentService.save(document);
        return ResultUtils.success(document);
    }

    /**
     * 上传文档（自动解析 + 嵌入 + 分块入库）
     */
    @PostMapping("/upload")
    public BaseResponse<String> upload(
            @RequestParam String kbId,
            @RequestParam MultipartFile file
    ) {
        return ResultUtils.success(documentFacadeService.uploadDocument(kbId, file));
    }

    @GetMapping("/{id}")
    public BaseResponse<Document> getById(@PathVariable String id) {
        return ResultUtils.success(documentService.getById(id));
    }

    @GetMapping
    public BaseResponse<List<Document>> list() {
        return ResultUtils.success(documentService.list());
    }

    @GetMapping("/kb/{kbId}")
    public BaseResponse<List<Document>> listByKbId(@PathVariable String kbId) {
        return ResultUtils.success(documentService.lambdaQuery().eq(Document::getKbId, kbId).list());
    }

    @PutMapping
    public BaseResponse<Document> update(@RequestBody Document document) {
        documentService.updateById(document);
        return ResultUtils.success(document);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        documentFacadeService.deleteDocument(id);
        return ResultUtils.success(true);
    }
}
