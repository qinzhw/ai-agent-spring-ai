package com.example.aiagent.agent;

import com.example.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;

import java.util.concurrent.Executor;

/**
 * 自定义智能体 MyManus.拥有自主规划能力,能够根据用户需求,主动选择合适的工具或工具组合来完成复杂任务.
 */
public class MyManus extends ToolCallAgent {

    public MyManus(ToolCallback[] allTools, ChatModel dashscopeChatModel, Executor executor) {
        super(allTools);
        this.setExecutor(executor);
        this.setName("MyManus");
        String systemPrompt = """
                You are MyManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(systemPrompt);
        String nextStepPrompt = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the terminate tool/function call.
                """;
        this.setNextStepPrompt(nextStepPrompt);
        this.setMaxSteps(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
