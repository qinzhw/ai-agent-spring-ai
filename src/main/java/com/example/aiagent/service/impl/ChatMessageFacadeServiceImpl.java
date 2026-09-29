package com.example.aiagent.service.impl;

import com.example.aiagent.converter.ChatMessageConverter;
import com.example.aiagent.exception.BusinessException;
import com.example.aiagent.exception.ErrorCode;
import com.example.aiagent.mapper.ChatMessageMapper;
import com.example.aiagent.model.dto.ChatMessageDTO;
import com.example.aiagent.model.entity.ChatMessage;
import com.example.aiagent.model.response.CreateChatMessageResponse;
import com.example.aiagent.model.vo.ChatMessageVO;
import com.example.aiagent.service.ChatMessageFacadeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天消息门面服务实现
 */
@Service
@AllArgsConstructor
public class ChatMessageFacadeServiceImpl implements ChatMessageFacadeService {

    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageConverter chatMessageConverter;

    @Override
    public List<ChatMessageVO> getMessagesBySessionId(String sessionId, int limit) {
        List<ChatMessage> messages = chatMessageMapper.selectBySessionIdRecently(sessionId, limit);
        return messages.stream()
                .map(chatMessageConverter::toVO)
                .toList();
    }

    @Override
    public CreateChatMessageResponse createMessage(ChatMessageDTO dto) {
        ChatMessage entity = chatMessageConverter.toEntity(dto);
        chatMessageMapper.insert(entity);
        return CreateChatMessageResponse.builder().chatMessageId(entity.getId()).build();
    }

    @Override
    public CreateChatMessageResponse createMessage(ChatMessage entity) {
        chatMessageMapper.insert(entity);
        return CreateChatMessageResponse.builder().chatMessageId(entity.getId()).build();
    }

    @Override
    public void deleteMessage(String messageId) {
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "消息不存在: " + messageId);
        }
        chatMessageMapper.deleteById(messageId);
    }

    @Override
    public void appendContent(String messageId, String appendContent) {
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "消息不存在: " + messageId);
        }
        String current = message.getContent() != null ? message.getContent() : "";
        message.setContent(current + appendContent);
        chatMessageMapper.updateById(message);
    }

    @Override
    public void updateContent(String messageId, String content) {
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "消息不存在: " + messageId);
        }
        message.setContent(content);
        chatMessageMapper.updateById(message);
    }
}
