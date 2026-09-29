package com.example.aiagent.agent.tool;

/**
 * 运行时工具接口
 */
public interface RuntimeTool {

    /**
     * 工具名称
     */
    String getName();

    /**
     * 工具描述
     */
    String getDescription();

    /**
     * 工具类型（FIXED / OPTIONAL）
     */
    ToolType getType();
}
