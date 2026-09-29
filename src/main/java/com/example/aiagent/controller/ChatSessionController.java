package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.entity.ChatSession;
import com.example.aiagent.service.ChatSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话管理接口
 */
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "会话管理")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @PostMapping
    public BaseResponse<ChatSession> create(@RequestBody ChatSession session) {
        chatSessionService.save(session);
        return ResultUtils.success(session);
    }

    @GetMapping("/{id}")
    public BaseResponse<ChatSession> getById(@PathVariable String id) {
        return ResultUtils.success(chatSessionService.getById(id));
    }

    @GetMapping
    public BaseResponse<List<ChatSession>> list() {
        return ResultUtils.success(chatSessionService.list());
    }

    @PutMapping
    public BaseResponse<ChatSession> update(@RequestBody ChatSession session) {
        chatSessionService.updateById(session);
        return ResultUtils.success(session);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return ResultUtils.success(chatSessionService.removeById(id));
    }
}
