package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息管理接口
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "消息管理")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping
    public BaseResponse<ChatMessage> create(@RequestBody ChatMessage message) {
        chatMessageService.save(message);
        return ResultUtils.success(message);
    }

    @GetMapping("/{id}")
    public BaseResponse<ChatMessage> getById(@PathVariable String id) {
        return ResultUtils.success(chatMessageService.getById(id));
    }

    @GetMapping("/session/{sessionId}")
    public BaseResponse<List<ChatMessage>> listBySessionId(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "20") int limit) {
        return ResultUtils.success(chatMessageService.selectBySessionIdRecently(sessionId, limit));
    }

    @PutMapping
    public BaseResponse<ChatMessage> update(@RequestBody ChatMessage message) {
        chatMessageService.updateById(message);
        return ResultUtils.success(message);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return ResultUtils.success(chatMessageService.removeById(id));
    }
}
