package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.entity.Tool;
import com.example.aiagent.service.ToolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工具管理接口
 */
@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
@Tag(name = "工具管理")
public class ToolController {

    private final ToolService toolService;

    @PostMapping
    public BaseResponse<Tool> create(@RequestBody Tool tool) {
        toolService.save(tool);
        return ResultUtils.success(tool);
    }

    @GetMapping("/{id}")
    public BaseResponse<Tool> getById(@PathVariable String id) {
        return ResultUtils.success(toolService.getById(id));
    }

    @GetMapping
    public BaseResponse<List<Tool>> list() {
        return ResultUtils.success(toolService.list());
    }

    @PutMapping
    public BaseResponse<Tool> update(@RequestBody Tool tool) {
        toolService.updateById(tool);
        return ResultUtils.success(tool);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return ResultUtils.success(toolService.removeById(id));
    }
}
