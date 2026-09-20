package com.example.aiagent.service;

import com.example.aiagent.agent.MyManus;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executor;

/**
 * 智能体服务
 * 负责按请求创建有状态的智能体实例并执行，Controller 只与本服务交互，不直接 new 智能体。
 */
@Service
public class AgentService {

    private final ToolCallback[] allTools;
    private final ChatModel dashscopeChatModel;
    private final Executor agentExecutor;

    public AgentService(ToolCallback[] allTools,
                        ChatModel dashscopeChatModel,
                        @Qualifier("agentExecutor") Executor agentExecutor) {
        this.allTools = allTools;
        this.dashscopeChatModel = dashscopeChatModel;
        this.agentExecutor = agentExecutor;
    }

    /**
     * 流式调用 MyManus 超级智能体
     *
     * @param message 用户输入
     * @return SSE 流式响应
     */
    public SseEmitter chatWithManus(String message) {
        MyManus myManus = new MyManus(allTools, dashscopeChatModel, agentExecutor);
        return myManus.runStream(message);
    }
}

