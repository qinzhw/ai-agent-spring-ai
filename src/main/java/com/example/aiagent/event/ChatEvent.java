package com.example.aiagent.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 聊天事件
 * 
 */
@Data
@AllArgsConstructor
public class ChatEvent {
    /** 智能体 ID */
    private String agentId;
    /** 会话 ID */
    private String sessionId;
    /** 用户输入文本 */
    private String userInput;
}
