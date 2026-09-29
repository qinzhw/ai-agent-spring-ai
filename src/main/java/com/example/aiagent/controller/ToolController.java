package com.example.aiagent.controller;

import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.vo.RuntimeToolVO;
import com.example.aiagent.service.ToolFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工具管理接口
 */
@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
@Tag(name = "工具管理")
public class ToolController {

    private final ToolFacadeService toolFacadeService;

    @GetMapping
    @Operation(summary = "获取所有可用工具列表")
    public BaseResponse<List<RuntimeToolVO>> listAll() {
        return ResultUtils.success(toVOList(toolFacadeService.getAllTools()));
    }

    @GetMapping("/fixed")
    @Operation(summary = "获取固定工具列表（所有 Agent 默认拥有）")
    public BaseResponse<List<RuntimeToolVO>> listFixed() {
        return ResultUtils.success(toVOList(toolFacadeService.getFixedTools()));
    }

    @GetMapping("/optional")
    @Operation(summary = "获取可选工具列表（供前端动态配置 Agent 时选择）")
    public BaseResponse<List<RuntimeToolVO>> listOptional() {
        return ResultUtils.success(toVOList(toolFacadeService.getOptionalTools()));
    }

    private List<RuntimeToolVO> toVOList(List<RuntimeTool> tools) {
        return tools.stream()
                .map(t -> RuntimeToolVO.builder()
                        .name(t.getName())
                        .description(t.getDescription())
                        .type(t.getType().name())
                        .build())
                .toList();
    }
}
