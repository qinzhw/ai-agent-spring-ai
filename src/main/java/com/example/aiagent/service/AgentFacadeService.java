package com.example.aiagent.service;

import com.example.aiagent.model.request.CreateAgentRequest;
import com.example.aiagent.model.request.UpdateAgentRequest;
import com.example.aiagent.model.response.CreateAgentResponse;
import com.example.aiagent.model.vo.AgentVO;

import java.util.List;

/**
 * 智能体门面服务
 */
public interface AgentFacadeService {

    List<AgentVO> getAgents();

    CreateAgentResponse createAgent(CreateAgentRequest request);

    void deleteAgent(String agentId);

    void updateAgent(String agentId, UpdateAgentRequest request);

    AgentVO getAgent(String agentId);
}
