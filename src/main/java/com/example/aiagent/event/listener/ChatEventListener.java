package com.example.aiagent.event.listener;

import com.example.aiagent.agent.AgentEngine;
import com.example.aiagent.agent.AgentFactory;
import com.example.aiagent.event.ChatEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 聊天事件监听器
 * 
 */
@Slf4j
@Component
@AllArgsConstructor
public class ChatEventListener {

    private final AgentFactory agentFactory;

    @Async("agentExecutor")
    @EventListener
    public void handle(ChatEvent event) {
        log.info("收到聊天事件: agentId={}, sessionId={}", event.getAgentId(), event.getSessionId());
        try {
            AgentEngine engine = agentFactory.create(event.getAgentId(), event.getSessionId());
            engine.run();
        } catch (Exception e) {
            log.error("Agent 执行异常: agentId={}, sessionId={}", event.getAgentId(), event.getSessionId(), e);
        }
    }
}
