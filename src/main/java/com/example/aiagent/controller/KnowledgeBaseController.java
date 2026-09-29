package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.request.CreateKnowledgeBaseRequest;
import com.example.aiagent.model.request.UpdateKnowledgeBaseRequest;
import com.example.aiagent.model.response.CreateKnowledgeBaseResponse;
import com.example.aiagent.model.vo.KnowledgeBaseVO;
import com.example.aiagent.service.KnowledgeBaseFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/knowledge-bases")
@RequiredArgsConstructor
@Tag(name = "知识库管理")
public class KnowledgeBaseController {

    private final KnowledgeBaseFacadeService knowledgeBaseFacadeService;

    @PostMapping
    @Operation(summary = "创建知识库")
    public BaseResponse<CreateKnowledgeBaseResponse> create(@RequestBody CreateKnowledgeBaseRequest request) {
        return ResultUtils.success(knowledgeBaseFacadeService.createKnowledgeBase(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取知识库详情")
    public BaseResponse<KnowledgeBaseVO> getById(@PathVariable String id) {
        return ResultUtils.success(knowledgeBaseFacadeService.getKnowledgeBase(id));
    }

    @GetMapping
    @Operation(summary = "获取知识库列表")
    public BaseResponse<List<KnowledgeBaseVO>> list() {
        return ResultUtils.success(knowledgeBaseFacadeService.getKnowledgeBases());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "更新知识库")
    public BaseResponse<Void> update(@PathVariable String id, @RequestBody UpdateKnowledgeBaseRequest request) {
        knowledgeBaseFacadeService.updateKnowledgeBase(id, request);
        return ResultUtils.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识库")
    public BaseResponse<Void> delete(@PathVariable String id) {
        knowledgeBaseFacadeService.deleteKnowledgeBase(id);
        return ResultUtils.success(null);
    }
}
