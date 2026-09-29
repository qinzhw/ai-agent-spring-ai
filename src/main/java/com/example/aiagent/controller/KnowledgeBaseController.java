package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.entity.KnowledgeBase;
import com.example.aiagent.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理接口
 */
@RestController
@RequestMapping("/knowledge-bases")
@RequiredArgsConstructor
@Tag(name = "知识库管理")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping
    public BaseResponse<KnowledgeBase> create(@RequestBody KnowledgeBase kb) {
        knowledgeBaseService.save(kb);
        return ResultUtils.success(kb);
    }

    @GetMapping("/{id}")
    public BaseResponse<KnowledgeBase> getById(@PathVariable String id) {
        return ResultUtils.success(knowledgeBaseService.getById(id));
    }

    @GetMapping
    public BaseResponse<List<KnowledgeBase>> list() {
        return ResultUtils.success(knowledgeBaseService.list());
    }

    @PutMapping
    public BaseResponse<KnowledgeBase> update(@RequestBody KnowledgeBase kb) {
        knowledgeBaseService.updateById(kb);
        return ResultUtils.success(kb);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return ResultUtils.success(knowledgeBaseService.removeById(id));
    }
}
