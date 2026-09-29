package com.example.aiagent.service.impl;

import com.example.aiagent.converter.ChatSessionConverter;
import com.example.aiagent.exception.BusinessException;
import com.example.aiagent.exception.ErrorCode;
import com.example.aiagent.model.entity.ChatSession;
import com.example.aiagent.model.request.CreateChatSessionRequest;
import com.example.aiagent.model.request.UpdateChatSessionRequest;
import com.example.aiagent.model.response.CreateChatSessionResponse;
import com.example.aiagent.model.vo.ChatSessionVO;
import com.example.aiagent.service.ChatSessionFacadeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天会话门面服务实现
 */
@Service
@AllArgsConstructor
public class ChatSessionFacadeServiceImpl implements ChatSessionFacadeService {

    private final com.example.aiagent.mapper.ChatSessionMapper chatSessionMapper;
    private final ChatSessionConverter chatSessionConverter;

    @Override
    public List<ChatSessionVO> getSessions() {
        List<ChatSession> sessions = chatSessionMapper.selectList(null);
        return sessions.stream().map(chatSessionConverter::toVO).toList();
    }

    @Override
    public List<ChatSessionVO> getSessionsByAgentId(String agentId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getAgentId, agentId).orderByDesc(ChatSession::getCreatedAt);
        List<ChatSession> sessions = chatSessionMapper.selectList(wrapper);
        return sessions.stream().map(chatSessionConverter::toVO).toList();
    }

    @Override
    public ChatSessionVO getSession(String sessionId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在: " + sessionId);
        }
        return chatSessionConverter.toVO(session);
    }

    @Override
    public CreateChatSessionResponse createSession(CreateChatSessionRequest request) {
        ChatSession session = new ChatSession();
        session.setAgentId(request.getAgentId());
        session.setTitle(request.getTitle());
        chatSessionMapper.insert(session);
        return CreateChatSessionResponse.builder().chatSessionId(session.getId()).build();
    }

    @Override
    public void updateSession(String sessionId, UpdateChatSessionRequest request) {
        ChatSession existing = chatSessionMapper.selectById(sessionId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在: " + sessionId);
        }
        if (request.getTitle() != null) existing.setTitle(request.getTitle());
        chatSessionMapper.updateById(existing);
    }

    @Override
    public void deleteSession(String sessionId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在: " + sessionId);
        }
        chatSessionMapper.deleteById(sessionId);
    }
}
