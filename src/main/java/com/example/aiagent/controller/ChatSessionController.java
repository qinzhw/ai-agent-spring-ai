package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.request.CreateChatSessionRequest;
import com.example.aiagent.model.request.UpdateChatSessionRequest;
import com.example.aiagent.model.response.CreateChatSessionResponse;
import com.example.aiagent.model.vo.ChatSessionVO;
import com.example.aiagent.service.ChatSessionFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-sessions")
@RequiredArgsConstructor
@Tag(name = "会话管理")
public class ChatSessionController {

    private final ChatSessionFacadeService chatSessionFacadeService;

    @PostMapping
    @Operation(summary = "创建会话")
    public BaseResponse<CreateChatSessionResponse> create(@RequestBody CreateChatSessionRequest request) {
        return ResultUtils.success(chatSessionFacadeService.createSession(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取会话详情")
    public BaseResponse<ChatSessionVO> getById(@PathVariable String id) {
        return ResultUtils.success(chatSessionFacadeService.getSession(id));
    }

    @GetMapping
    @Operation(summary = "获取会话列表")
    public BaseResponse<List<ChatSessionVO>> list() {
        return ResultUtils.success(chatSessionFacadeService.getSessions());
    }

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "按智能体获取会话列表")
    public BaseResponse<List<ChatSessionVO>> listByAgentId(@PathVariable String agentId) {
        return ResultUtils.success(chatSessionFacadeService.getSessionsByAgentId(agentId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "更新会话")
    public BaseResponse<Void> update(@PathVariable String id, @RequestBody UpdateChatSessionRequest request) {
        chatSessionFacadeService.updateSession(id, request);
        return ResultUtils.success(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除会话")
    public BaseResponse<Void> delete(@PathVariable String id) {
        chatSessionFacadeService.deleteSession(id);
        return ResultUtils.success(null);
    }
}
