package com.example.aiagent.controller;

import com.example.aiagent.app.LoveApp;
import com.example.aiagent.dto.ChatRequest;
import com.example.aiagent.service.AgentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private AgentService agentService;

    /**
     * AI 同步对话（SSE 流式输出）
     *
     * @param request 对话请求
     * @return 流式响应
     */
    @PostMapping(value = "/love_app/chat/sync/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSyncSSE(@Valid @RequestBody ChatRequest request) {
        return loveApp.doChatByStream(request.getMessage(), request.getChatId());
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param request 对话请求
     * @return SSE 流式响应
     */
    @PostMapping("/manus/chat")
    public SseEmitter doChatWithManus(@Valid @RequestBody ChatRequest request) {
        return agentService.chatWithManus(request.getMessage());
    }

}
