package com.example.aiagent.service.impl;

import com.example.aiagent.converter.KnowledgeBaseConverter;
import com.example.aiagent.exception.BusinessException;
import com.example.aiagent.exception.ErrorCode;
import com.example.aiagent.mapper.KnowledgeBaseMapper;
import com.example.aiagent.model.entity.KnowledgeBase;
import com.example.aiagent.model.request.CreateKnowledgeBaseRequest;
import com.example.aiagent.model.request.UpdateKnowledgeBaseRequest;
import com.example.aiagent.model.response.CreateKnowledgeBaseResponse;
import com.example.aiagent.model.vo.KnowledgeBaseVO;
import com.example.aiagent.service.KnowledgeBaseFacadeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识库门面服务实现
 */
@Service
@AllArgsConstructor
public class KnowledgeBaseFacadeServiceImpl implements KnowledgeBaseFacadeService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBaseConverter knowledgeBaseConverter;

    @Override
    public List<KnowledgeBaseVO> getKnowledgeBases() {
        List<KnowledgeBase> list = knowledgeBaseMapper.selectList(null);
        return list.stream().map(knowledgeBaseConverter::toVO).toList();
    }

    @Override
    public KnowledgeBaseVO getKnowledgeBase(String kbId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "知识库不存在: " + kbId);
        }
        return knowledgeBaseConverter.toVO(kb);
    }

    @Override
    public CreateKnowledgeBaseResponse createKnowledgeBase(CreateKnowledgeBaseRequest request) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName(request.getName());
        kb.setDescription(request.getDescription());
        knowledgeBaseMapper.insert(kb);
        return CreateKnowledgeBaseResponse.builder().knowledgeBaseId(kb.getId()).build();
    }

    @Override
    public void updateKnowledgeBase(String kbId, UpdateKnowledgeBaseRequest request) {
        KnowledgeBase existing = knowledgeBaseMapper.selectById(kbId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "知识库不存在: " + kbId);
        }
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        knowledgeBaseMapper.updateById(existing);
    }

    @Override
    public void deleteKnowledgeBase(String kbId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "知识库不存在: " + kbId);
        }
        knowledgeBaseMapper.deleteById(kbId);
    }
}
