package com.example.aiagent.agent.tool;

/**
 * 工具类型枚举
 * FIXED  —— 所有 Agent 默认拥有的基础工具（如终止工具）
 * OPTIONAL —— 可按 Agent 配置选择启用的工具
 */
public enum ToolType {
    FIXED,
    OPTIONAL,
}
