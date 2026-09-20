package com.example.aiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executor;

@SpringBootTest
class MyManusTest {

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    @Qualifier("agentExecutor")
    private Executor agentExecutor;

    @Test
    void runStreamReturnsEmitter() {
        MyManus myManus = new MyManus(allTools, dashscopeChatModel, agentExecutor);
        SseEmitter emitter = myManus.runStream("你好");
        Assertions.assertNotNull(emitter);
    }
}
