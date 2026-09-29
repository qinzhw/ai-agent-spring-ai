package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.request.CreateAgentRequest;
import com.example.aiagent.model.request.UpdateAgentRequest;
import com.example.aiagent.model.response.CreateAgentResponse;
import com.example.aiagent.model.vo.AgentVO;
import com.example.aiagent.service.AgentFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@Tag(name = "智能体管理")
public class AgentController {

    private final AgentFacadeService agentFacadeService;

    @PostMapping
    @Operation(summary = "创建智能体")
    public BaseResponse<CreateAgentResponse> create(@RequestBody CreateAgentRequest request) {
        return ResultUtils.success(agentFacadeService.createAgent(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取智能体详情")
    public BaseResponse<AgentVO> getById(@PathVariable String id) {
        return ResultUtils.success(agentFacadeService.getAgent(id));
    }

    @GetMapping
    @Operation(summary = "获取智能体列表")
    public BaseResponse<List<AgentVO>> list() {
        return ResultUtils.success(agentFacadeService.getAgents());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "更新智能体（部分更新）")
    public BaseResponse<Void> update(@PathVariable String id, @RequestBody UpdateAgentRequest request) {
        agentFacadeService.updateAgent(id, request);
        return ResultUtils.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除智能体")
    public BaseResponse<Void> delete(@PathVariable String id) {
        agentFacadeService.deleteAgent(id);
        return ResultUtils.success(null);
    }
}
