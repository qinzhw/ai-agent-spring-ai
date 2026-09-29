package com.example.aiagent.service;

import com.example.aiagent.model.request.CreateChatSessionRequest;
import com.example.aiagent.model.request.UpdateChatSessionRequest;
import com.example.aiagent.model.response.CreateChatSessionResponse;
import com.example.aiagent.model.vo.ChatSessionVO;

import java.util.List;

/**
 * 聊天会话门面服务
 */
public interface ChatSessionFacadeService {

    List<ChatSessionVO> getSessions();

    List<ChatSessionVO> getSessionsByAgentId(String agentId);

    ChatSessionVO getSession(String sessionId);

    CreateChatSessionResponse createSession(CreateChatSessionRequest request);

    void updateSession(String sessionId, UpdateChatSessionRequest request);

    void deleteSession(String sessionId);
}
