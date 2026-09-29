package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.event.ChatEvent;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天入口
 * 
 * 接收用户消息 → 持久化 → 发布 ChatEvent → Agent 异步处理
 * 前端通过 SSE 端点实时接收 Agent 的执行结果。
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "聊天管理")
public class ChatController {

    private final ApplicationEventPublisher eventPublisher;
    private final ChatMessageService chatMessageService;

    /**
     * 发送聊天消息
     *
     * @param agentId 智能体 ID
     * @param request 聊天请求
     * @return 保存的用户消息
     */
    @PostMapping("/{agentId}/send")
    @Operation(summary = "发送聊天消息", description = "发送消息给指定智能体，Agent 将异步处理并通过 SSE 推送结果")
    public BaseResponse<ChatMessage> send(
            @PathVariable String agentId,
            @RequestBody ChatRequest request) {

        // 1. 持久化用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(request.sessionId());
        userMessage.setRole("user");
        userMessage.setContent(request.userInput());
        chatMessageService.save(userMessage);

        // 2. 发布聊天事件（异步处理）
        eventPublisher.publishEvent(new ChatEvent(agentId, request.sessionId(), request.userInput()));

        return ResultUtils.success(userMessage);
    }

    /**
     * 聊天请求体
     */
    public record ChatRequest(
            /** 会话 ID */
            String sessionId,
            /** 用户输入 */
            String userInput
    ) {}
}
