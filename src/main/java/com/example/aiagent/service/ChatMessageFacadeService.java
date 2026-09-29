package com.example.aiagent.service;

import com.example.aiagent.model.dto.ChatMessageDTO;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.model.response.CreateChatMessageResponse;
import com.example.aiagent.model.vo.ChatMessageVO;

import java.util.List;

/**
 * 聊天消息门面服务
 */
public interface ChatMessageFacadeService {

    List<ChatMessageVO> getMessagesBySessionId(String sessionId, int limit);

    CreateChatMessageResponse createMessage(ChatMessageDTO dto);

    CreateChatMessageResponse createMessage(ChatMessage entity);

    /**
     * 追加内容到已有消息末尾（用于 SSE 流式场景）
     */
    void appendContent(String messageId, String appendContent);

    /**
     * 更新消息内容（全量替换）
     */
    void updateContent(String messageId, String content);

    void deleteMessage(String messageId);
}
