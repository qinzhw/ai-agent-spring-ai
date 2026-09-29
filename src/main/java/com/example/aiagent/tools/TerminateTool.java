package com.example.aiagent.tools;

import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.agent.tool.ToolType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class TerminateTool implements RuntimeTool {

    @Override
    public String getName() {
        return "terminate";
    }

    @Override
    public String getDescription() {
        return "终止 Agent Loop，当所有任务执行完毕或无法继续时调用";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @Tool(name = "terminate", description = "如果你觉得当前所有的任务已经执行完毕了，就执行这个工具调用")
    public String doTerminate() {
        return "任务结束";
    }
}
