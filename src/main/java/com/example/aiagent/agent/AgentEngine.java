package com.example.aiagent.agent;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.aiagent.agent.enums.AgentState;
import com.example.aiagent.message.SseMessage;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.service.ChatMessageService;
import com.example.aiagent.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Agent 运行引擎
 * 
 * 实现 think → execute 的 ReAct 循环，支持手动工具调用、
 * 消息持久化和 SSE 实时推送。
 */
@Slf4j
public class AgentEngine {

    private final String agentId;
    private final String name;
    private final String description;
    private final String systemPrompt;
    private final ChatClient chatClient;
    private final List<ToolCallback> availableTools;
    private final String chatSessionId;
    private final SseService sseService;
    private final ChatMessageService chatMessageService;
    private final ChatMemory chatMemory;
    private final ToolCallingManager toolCallingManager;
    private final ChatOptions chatOptions;

    private AgentState agentState;
    private ChatResponse lastChatResponse;
    private final List<ChatMessage> pendingMessages = new ArrayList<>();

    private static final int MAX_STEPS = 20;
    private static final int DEFAULT_MAX_MESSAGES = 20;

    public AgentEngine(String agentId,
                       String name,
                       String description,
                       String systemPrompt,
                       ChatClient chatClient,
                       Integer maxMessages,
                       List<Message> memory,
                       List<ToolCallback> availableTools,
                       String chatSessionId,
                       SseService sseService,
                       ChatMessageService chatMessageService) {
        this.agentId = agentId;
        this.name = name;
        this.description = description;
        this.systemPrompt = systemPrompt;
        this.chatClient = chatClient;
        this.availableTools = availableTools;
        this.chatSessionId = chatSessionId;
        this.sseService = sseService;
        this.chatMessageService = chatMessageService;

        this.agentState = AgentState.IDLE;

        // 恢复历史记忆
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(maxMessages == null ? DEFAULT_MAX_MESSAGES : maxMessages)
                .build();
        this.chatMemory.add(chatSessionId, memory);

        // 添加系统提示
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            this.chatMemory.add(chatSessionId, new SystemMessage(systemPrompt));
        }

        // 关闭 Spring AI 内置的自动工具执行，改为手动管理
        this.chatOptions = DefaultToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();

        this.toolCallingManager = ToolCallingManager.builder().build();
    }

    // ========================= Agent Loop =========================

    /**
     * 启动 Agent 主循环
     */
    public void run() {
        if (agentState != AgentState.IDLE) {
            throw new IllegalStateException("Agent is not idle");
        }
        agentState = AgentState.RUNNING;

        try {
            for (int i = 0; i < MAX_STEPS && agentState != AgentState.FINISHED; i++) {
                int currentStep = i + 1;
                log.info("===== Agent [{}] Step {}/{} =====", name, currentStep, MAX_STEPS);
                step();
                if (currentStep >= MAX_STEPS) {
                    agentState = AgentState.FINISHED;
                    log.warn("达到最大步数限制，停止 Agent");
                }
            }
            agentState = AgentState.FINISHED;
            sendDone();
        } catch (Exception e) {
            agentState = AgentState.ERROR;
            log.error("Agent 运行异常", e);
            throw new RuntimeException("Agent 运行异常", e);
        }
    }

    /**
     * 单步：think → execute
     */
    private void step() {
        if (think()) {
            execute();
        } else {
            // 无工具调用，任务结束
            agentState = AgentState.FINISHED;
        }
    }

    // ========================= Think =========================

    /**
     * 调用模型进行决策，返回是否需要执行工具
     */
    private boolean think() {
        agentState = AgentState.THINKING;

        String thinkPrompt = """
                现在你是一个智能体的具体「决策模块」
                请根据当前对话上下文，决定下一步的动作。
                """;

        Prompt prompt = Prompt.builder()
                .chatOptions(this.chatOptions)
                .messages(this.chatMemory.get(this.chatSessionId))
                .build();

        this.lastChatResponse = this.chatClient
                .prompt(prompt)
                .system(thinkPrompt)
                .toolCallbacks(this.availableTools.toArray(new ToolCallback[0]))
                .call()
                .chatClientResponse()
                .chatResponse();

        Assert.notNull(lastChatResponse, "Last chat response cannot be null");

        AssistantMessage output = this.lastChatResponse.getResult().getOutput();
        List<AssistantMessage.ToolCall> toolCalls = output.getToolCalls();

        // 持久化 + SSE 推送
        saveMessage(output);
        flushPendingMessages();

        logToolCalls(toolCalls);

        return !toolCalls.isEmpty();
    }

    // ========================= Execute =========================

    /**
     * 执行模型返回的工具调用
     */
    private void execute() {
        agentState = AgentState.EXECUTING;
        Assert.notNull(this.lastChatResponse, "Last chat response cannot be null");

        if (!this.lastChatResponse.hasToolCalls()) {
            return;
        }

        Prompt prompt = Prompt.builder()
                .messages(this.chatMemory.get(this.chatSessionId))
                .chatOptions(this.chatOptions)
                .build();

        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, this.lastChatResponse);

        // 更新记忆：清除旧历史，加入工具执行后的完整对话历史
        this.chatMemory.clear(this.chatSessionId);
        this.chatMemory.add(this.chatSessionId, toolExecutionResult.conversationHistory());

        // 提取工具返回结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) toolExecutionResult
                .conversationHistory()
                .get(toolExecutionResult.conversationHistory().size() - 1);

        String resultSummary = toolResponseMessage.getResponses().stream()
                .map(resp -> "工具 " + resp.name() + " 返回: " + resp.responseData())
                .collect(Collectors.joining("\n"));
        log.info("工具调用结果：{}", resultSummary);

        // 持久化 + SSE 推送
        saveMessage(toolResponseMessage);
        flushPendingMessages();

        // 检查是否调用了 terminate 工具
        if (toolResponseMessage.getResponses().stream()
                .anyMatch(resp -> "terminate".equals(resp.name()))) {
            this.agentState = AgentState.FINISHED;
            log.info("收到 terminate 信号，任务结束");
        }
    }

    // ========================= 持久化 & SSE =========================

    /**
     * 将 AI 返回的 Message 持久化到数据库，并加入 pending 队列等待 SSE 推送
     */
    private void saveMessage(Message message) {
        if (message instanceof AssistantMessage assistantMessage) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(this.chatSessionId);
            chatMessage.setRole("assistant");
            chatMessage.setContent(assistantMessage.getText());

            // 将 toolCalls 存入 metadata
            if (assistantMessage.getToolCalls() != null && !assistantMessage.getToolCalls().isEmpty()) {
                JSONArray toolCallsJson = new JSONArray();
                for (AssistantMessage.ToolCall tc : assistantMessage.getToolCalls()) {
                    JSONObject tcJson = new JSONObject();
                    tcJson.set("id", tc.id());
                    tcJson.set("type", tc.type());
                    tcJson.set("name", tc.name());
                    tcJson.set("arguments", tc.arguments());
                    toolCallsJson.add(tcJson);
                }
                JSONObject meta = new JSONObject();
                meta.set("toolCalls", toolCallsJson);
                chatMessage.setMetadata(meta.toString());
            }

            chatMessageService.save(chatMessage);
            pendingMessages.add(chatMessage);

        } else if (message instanceof ToolResponseMessage toolResponseMessage) {
            for (ToolResponseMessage.ToolResponse toolResponse : toolResponseMessage.getResponses()) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSessionId(this.chatSessionId);
                chatMessage.setRole("tool");
                chatMessage.setContent(toolResponse.responseData());

                JSONObject meta = new JSONObject();
                meta.set("toolName", toolResponse.name());
                meta.set("toolCallId", toolResponse.id());
                chatMessage.setMetadata(meta.toString());

                chatMessageService.save(chatMessage);
                pendingMessages.add(chatMessage);
            }
        }
    }

    /**
     * 将 pending 队列中的消息通过 SSE 推送给前端
     */
    private void flushPendingMessages() {
        for (ChatMessage msg : pendingMessages) {
            SseMessage sseMessage = SseMessage.builder()
                    .type(SseMessage.Type.AI_GENERATED_CONTENT)
                    .payload(SseMessage.Payload.builder()
                            .content(msg.getContent())
                            .role(msg.getRole())
                            .build())
                    .metadata(SseMessage.Metadata.builder()
                            .chatMessageId(msg.getId())
                            .build())
                    .build();
            sseService.send(this.chatSessionId, sseMessage);
        }
        pendingMessages.clear();
    }

    /**
     * 发送完成信号
     */
    private void sendDone() {
        SseMessage doneMsg = SseMessage.builder()
                .type(SseMessage.Type.AI_DONE)
                .payload(SseMessage.Payload.builder()
                        .done(true)
                        .statusText("完成")
                        .build())
                .build();
        sseService.send(this.chatSessionId, doneMsg);
    }

    // ========================= 辅助方法 =========================

    private void logToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            log.info("[ToolCalling] 无工具调用");
            return;
        }
        String logMessage = IntStream.range(0, toolCalls.size())
                .mapToObj(i -> {
                    AssistantMessage.ToolCall call = toolCalls.get(i);
                    return String.format(
                            "[ToolCalling #%d]\n- name      : %s\n- arguments : %s",
                            i + 1, call.name(), call.arguments());
                })
                .collect(Collectors.joining("\n\n"));
        log.info("\n========== Tool Calling ==========\n{}\n=================================\n", logMessage);
    }

    @Override
    public String toString() {
        return "AgentEngine{name='%s', agentId='%s', systemPrompt='%s'}".formatted(name, agentId, systemPrompt);
    }
}
