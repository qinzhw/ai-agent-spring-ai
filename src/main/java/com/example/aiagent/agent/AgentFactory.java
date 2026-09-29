package com.example.aiagent.agent;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.config.ChatClientRegistry;
import com.example.aiagent.model.entity.Agent;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.mapper.AgentMapper;
import com.example.aiagent.service.ChatMessageService;
import com.example.aiagent.service.SseService;
import com.example.aiagent.service.ToolFacadeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Agent 工厂
 * 
 */
@Slf4j
@Component
public class AgentFactory {

    private final ChatClientRegistry chatClientRegistry;
    private final SseService sseService;
    private final AgentMapper agentMapper;
    private final ToolFacadeService toolFacadeService;
    private final ChatMessageService chatMessageService;

    public AgentFactory(ChatClientRegistry chatClientRegistry,
                        SseService sseService,
                        AgentMapper agentMapper,
                        ToolFacadeService toolFacadeService,
                        ChatMessageService chatMessageService) {
        this.chatClientRegistry = chatClientRegistry;
        this.sseService = sseService;
        this.agentMapper = agentMapper;
        this.toolFacadeService = toolFacadeService;
        this.chatMessageService = chatMessageService;
    }

    /**
     * 创建一个 AgentEngine 运行时实例
     *
     * @param agentId       智能体 ID
     * @param chatSessionId 会话 ID
     * @return 可运行的 AgentEngine
     */
    public AgentEngine create(String agentId, String chatSessionId) {
        // 1. 加载 Agent 配置
        Agent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent 不存在: " + agentId);
        }

        // 2. 恢复历史记忆
        List<Message> memory = loadMemory(chatSessionId);

        // 3. 解析运行时工具
        List<RuntimeTool> runtimeTools = resolveRuntimeTools(agent);
        List<ToolCallback> toolCallbacks = buildToolCallbacks(runtimeTools);

        // 4. 获取 ChatClient
        ChatClient chatClient = chatClientRegistry.get(agent.getModel());

        // 5. 解析 maxMessages
        Integer maxMessages = resolveMaxMessages(agent);

        log.info("创建 AgentEngine: agent={}, model={}, tools={}",
                agent.getName(), agent.getModel(),
                runtimeTools.stream().map(RuntimeTool::getName).toList());

        return new AgentEngine(
                agent.getId(),
                agent.getName(),
                agent.getDescription(),
                agent.getSystemPrompt(),
                chatClient,
                maxMessages,
                memory,
                toolCallbacks,
                chatSessionId,
                sseService,
                chatMessageService
        );
    }

    // ========================= 记忆恢复 =========================

    /**
     * 从数据库恢复聊天记忆为 Spring AI Message 列表
     */
    private List<Message> loadMemory(String chatSessionId) {
        List<ChatMessage> chatMessages = chatMessageService.selectBySessionIdRecently(chatSessionId, 20);
        List<Message> memory = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            String role = chatMessage.getRole();
            String content = chatMessage.getContent();

            switch (role) {
                case "system":
                    if (!StringUtils.hasLength(content)) continue;
                    memory.add(0, new SystemMessage(content));
                    break;
                case "user":
                    if (!StringUtils.hasLength(content)) continue;
                    memory.add(new UserMessage(content));
                    break;
                case "assistant":
                    List<AssistantMessage.ToolCall> toolCalls = parseToolCalls(chatMessage.getMetadata());
                    memory.add(AssistantMessage.builder()
                            .content(content)
                            .toolCalls(toolCalls)
                            .build());
                    break;
                case "tool":
                    ToolResponseMessage.ToolResponse toolResp = parseToolResponse(chatMessage.getMetadata(), content);
                    memory.add(ToolResponseMessage.builder()
                            .responses(List.of(toolResp))
                            .build());
                    break;
                default:
                    log.warn("未知的消息角色: {}, content={}", role, content);
            }
        }
        return memory;
    }

    /**
     * 从 metadata JSON 中解析 toolCalls
     */
    private List<AssistantMessage.ToolCall> parseToolCalls(String metadataJson) {
        if (!StringUtils.hasLength(metadataJson)) {
            return Collections.emptyList();
        }
        try {
            JSONObject meta = JSONUtil.parseObj(metadataJson);
            JSONArray toolCallsJson = meta.getJSONArray("toolCalls");
            if (toolCallsJson == null || toolCallsJson.isEmpty()) {
                return Collections.emptyList();
            }
            List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
            for (int i = 0; i < toolCallsJson.size(); i++) {
                JSONObject tc = toolCallsJson.getJSONObject(i);
                toolCalls.add(new AssistantMessage.ToolCall(
                        tc.getStr("id"),
                        tc.getStr("type", "function"),
                        tc.getStr("name"),
                        tc.getStr("arguments")
                ));
            }
            return toolCalls;
        } catch (Exception e) {
            log.warn("解析 toolCalls metadata 失败: {}", metadataJson, e);
            return Collections.emptyList();
        }
    }

    /**
     * 从 metadata JSON 中解析 toolResponse
     */
    private ToolResponseMessage.ToolResponse parseToolResponse(String metadataJson, String content) {
        String toolName = "unknown";
        String toolCallId = "";
        if (StringUtils.hasLength(metadataJson)) {
            try {
                JSONObject meta = JSONUtil.parseObj(metadataJson);
                toolName = meta.getStr("toolName", "unknown");
                toolCallId = meta.getStr("toolCallId", "");
            } catch (Exception e) {
                log.warn("解析 toolResponse metadata 失败: {}", metadataJson, e);
            }
        }
        return new ToolResponseMessage.ToolResponse(toolCallId, toolName, content);
    }

    // ========================= 工具解析 =========================

    /**
     * 根据 Agent 配置解析运行时工具列表
     */
    private List<RuntimeTool> resolveRuntimeTools(Agent agent) {
        // 固定工具（所有 Agent 默认拥有）
        List<RuntimeTool> runtimeTools = new ArrayList<>(toolFacadeService.getFixedTools());

        // 可选工具（按 Agent 配置）
        List<String> allowedToolNames = parseAllowedTools(agent.getAllowedTools());
        if (allowedToolNames != null && !allowedToolNames.isEmpty()) {
            Map<String, RuntimeTool> optionalToolMap = toolFacadeService.getOptionalTools().stream()
                    .collect(Collectors.toMap(RuntimeTool::getName, Function.identity()));

            for (String toolName : allowedToolNames) {
                RuntimeTool tool = optionalToolMap.get(toolName);
                if (tool != null) {
                    runtimeTools.add(tool);
                } else {
                    log.warn("Agent [{}] 配置了未知工具: {}", agent.getName(), toolName);
                }
            }
        }
        return runtimeTools;
    }

    /**
     * 解析 Agent.allowedTools JSON 数组为工具名称列表
     */
    private List<String> parseAllowedTools(String allowedToolsJson) {
        if (!StringUtils.hasLength(allowedToolsJson)) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.toList(allowedToolsJson, String.class);
        } catch (Exception e) {
            log.warn("解析 allowedTools 失败: {}", allowedToolsJson, e);
            return Collections.emptyList();
        }
    }

    /**
     * 将 RuntimeTool Bean 转换为 Spring AI ToolCallback
     */
    private List<ToolCallback> buildToolCallbacks(List<RuntimeTool> runtimeTools) {
        List<ToolCallback> callbacks = new ArrayList<>();
        for (RuntimeTool tool : runtimeTools) {
            Object target = resolveToolTarget(tool);
            ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                    .toolObjects(target)
                    .build()
                    .getToolCallbacks();
            callbacks.addAll(Arrays.asList(toolCallbacks));
        }
        return callbacks;
    }

    /**
     * 解析工具目标对象（处理 AOP 代理）
     */
    private Object resolveToolTarget(RuntimeTool tool) {
        try {
            return AopUtils.isAopProxy(tool)
                    ? AopUtils.getTargetClass(tool)
                    : tool;
        } catch (Exception e) {
            throw new IllegalStateException("解析工具目标对象失败: " + tool.getName(), e);
        }
    }

    // ========================= 配置解析 =========================

    /**
     * 从 Agent.chatOptions JSON 中解析 maxMessages
     */
    private Integer resolveMaxMessages(Agent agent) {
        if (!StringUtils.hasLength(agent.getChatOptions())) {
            return null;
        }
        try {
            JSONObject options = JSONUtil.parseObj(agent.getChatOptions());
            return options.getInt("messageLength", null);
        } catch (Exception e) {
            log.warn("解析 chatOptions 失败: {}", agent.getChatOptions(), e);
            return null;
        }
    }
}
