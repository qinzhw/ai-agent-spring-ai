package com.example.aiagent.service.impl;

import com.example.aiagent.converter.AgentConverter;
import com.example.aiagent.exception.BusinessException;
import com.example.aiagent.exception.ErrorCode;
import com.example.aiagent.mapper.AgentMapper;
import com.example.aiagent.model.dto.AgentDTO;
import com.example.aiagent.model.entity.Agent;
import com.example.aiagent.model.request.CreateAgentRequest;
import com.example.aiagent.model.request.UpdateAgentRequest;
import com.example.aiagent.model.response.CreateAgentResponse;
import com.example.aiagent.model.vo.AgentVO;
import com.example.aiagent.service.AgentFacadeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 智能体门面服务实现
 */
@Service
@AllArgsConstructor
public class AgentFacadeServiceImpl implements AgentFacadeService {

    private final AgentMapper agentMapper;
    private final AgentConverter agentConverter;

    @Override
    public List<AgentVO> getAgents() {
        List<Agent> agents = agentMapper.selectList(null);
        return agents.stream()
                .map(agentConverter::toVO)
                .toList();
    }

    @Override
    public AgentVO getAgent(String agentId) {
        Agent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "智能体不存在: " + agentId);
        }
        return agentConverter.toVO(agent);
    }

    @Override
    public CreateAgentResponse createAgent(CreateAgentRequest request) {
        AgentDTO dto = agentConverter.toDTO(request);
        Agent agent = agentConverter.toEntity(dto);
        agentMapper.insert(agent);
        return CreateAgentResponse.builder().agentId(agent.getId()).build();
    }

    @Override
    public void deleteAgent(String agentId) {
        Agent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "智能体不存在: " + agentId);
        }
        agentMapper.deleteById(agentId);
    }

    @Override
    public void updateAgent(String agentId, UpdateAgentRequest request) {
        Agent existing = agentMapper.selectById(agentId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "智能体不存在: " + agentId);
        }

        AgentDTO dto = agentConverter.toDTO(existing);
        agentConverter.updateDTOFromRequest(dto, request);
        Agent updated = agentConverter.toEntity(dto);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        agentMapper.updateById(updated);
    }
}
