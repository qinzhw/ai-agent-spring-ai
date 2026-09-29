package com.example.aiagent.service.impl;

import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.agent.tool.ToolType;
import com.example.aiagent.service.ToolFacadeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工具门面服务实现
 *
 */
@Service
@AllArgsConstructor
public class ToolFacadeServiceImpl implements ToolFacadeService {

    private final List<RuntimeTool> tools;

    @Override
    public List<RuntimeTool> getAllTools() {
        return tools;
    }

    @Override
    public List<RuntimeTool> getOptionalTools() {
        return getToolsByType(ToolType.OPTIONAL);
    }

    @Override
    public List<RuntimeTool> getFixedTools() {
        return getToolsByType(ToolType.FIXED);
    }

    private List<RuntimeTool> getToolsByType(ToolType type) {
        return tools.stream()
                .filter(tool -> tool.getType().equals(type))
                .toList();
    }
}
