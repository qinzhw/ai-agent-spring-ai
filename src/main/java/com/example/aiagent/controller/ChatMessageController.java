package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import com.example.aiagent.model.vo.ChatMessageVO;
import com.example.aiagent.service.ChatMessageFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "消息管理")
public class ChatMessageController {

    private final ChatMessageFacadeService chatMessageFacadeService;

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "获取会话消息列表")
    public BaseResponse<List<ChatMessageVO>> listBySessionId(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "20") int limit) {
        return ResultUtils.success(chatMessageFacadeService.getMessagesBySessionId(sessionId, limit));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息")
    public BaseResponse<Void> delete(@PathVariable String id) {
        chatMessageFacadeService.deleteMessage(id);
        return ResultUtils.success(null);
    }

    @PatchMapping("/{id}/append")
    @Operation(summary = "追加内容到消息末尾（SSE 流式场景）")
    public BaseResponse<Void> append(@PathVariable String id, @RequestBody AppendRequest request) {
        chatMessageFacadeService.appendContent(id, request.content());
        return ResultUtils.success(null);
    }

    @PatchMapping("/{id}/content")
    @Operation(summary = "更新消息内容（全量替换）")
    public BaseResponse<Void> updateContent(@PathVariable String id, @RequestBody AppendRequest request) {
        chatMessageFacadeService.updateContent(id, request.content());
        return ResultUtils.success(null);
    }

    public record AppendRequest(String content) {}
}
