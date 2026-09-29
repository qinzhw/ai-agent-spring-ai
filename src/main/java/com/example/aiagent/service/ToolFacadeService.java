package com.example.aiagent.service;

import com.example.aiagent.agent.tool.RuntimeTool;

import java.util.List;

/**
 * 工具门面服务
 *
 */
public interface ToolFacadeService {

    /**
     * 获取所有已注册的工具
     */
    List<RuntimeTool> getAllTools();

    /**
     * 获取可选工具列表
     */
    List<RuntimeTool> getOptionalTools();

    /**
     * 获取固定工具列表（所有 Agent 默认拥有）
     */
    List<RuntimeTool> getFixedTools();
}
