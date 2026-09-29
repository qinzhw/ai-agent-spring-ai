package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.entity.Agent;
import com.example.aiagent.service.AgentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能体管理接口
 */
@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@Tag(name = "智能体管理")
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public BaseResponse<Agent> create(@RequestBody Agent agent) {
        agentService.save(agent);
        return ResultUtils.success(agent);
    }

    @GetMapping("/{id}")
    public BaseResponse<Agent> getById(@PathVariable String id) {
        return ResultUtils.success(agentService.getById(id));
    }

    @GetMapping
    public BaseResponse<List<Agent>> list() {
        return ResultUtils.success(agentService.list());
    }

    @PutMapping
    public BaseResponse<Agent> update(@RequestBody Agent agent) {
        agentService.updateById(agent);
        return ResultUtils.success(agent);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return ResultUtils.success(agentService.removeById(id));
    }
}
