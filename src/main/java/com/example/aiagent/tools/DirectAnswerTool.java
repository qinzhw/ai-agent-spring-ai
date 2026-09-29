package com.example.aiagent.tools;

import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.agent.tool.ToolType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 直接回答工具
 * 当用户的问题不需要调用其他工具时，Agent 可用此工具直接返回自然语言回答。
 */
@Component
public class DirectAnswerTool implements RuntimeTool {

    @Override
    public String getName() {
        return "directAnswer";
    }

    @Override
    public String getDescription() {
        return "直接回答用户问题，适用于无需调用其他工具的场景";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @Tool(name = "directAnswer", description = "用于直接回答用户问题，适用于无需生成任务计划或调用其他工具的场景。")
    public String directAnswer(@ToolParam(description = "直接回答的内容") String answer) {
        return answer;
    }
}
