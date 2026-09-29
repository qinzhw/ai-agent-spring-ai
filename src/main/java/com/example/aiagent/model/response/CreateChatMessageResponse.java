package com.example.aiagent.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * 创建聊天消息响应
 */
@Data
@Builder
public class CreateChatMessageResponse {
    private String chatMessageId;
}
