package com.example.aiagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * AI 对话请求 DTO
 */
@Data
public class ChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户输入消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 会话 ID（用于对话记忆；不传则由服务端生成或忽略）
     */
    private String chatId;
}
