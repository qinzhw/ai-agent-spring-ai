package com.example.aiagent.service;

import com.example.aiagent.model.request.CreateKnowledgeBaseRequest;
import com.example.aiagent.model.request.UpdateKnowledgeBaseRequest;
import com.example.aiagent.model.response.CreateKnowledgeBaseResponse;
import com.example.aiagent.model.vo.KnowledgeBaseVO;

import java.util.List;

/**
 * 知识库门面服务
 */
public interface KnowledgeBaseFacadeService {

    List<KnowledgeBaseVO> getKnowledgeBases();

    KnowledgeBaseVO getKnowledgeBase(String kbId);

    CreateKnowledgeBaseResponse createKnowledgeBase(CreateKnowledgeBaseRequest request);

    void updateKnowledgeBase(String kbId, UpdateKnowledgeBaseRequest request);

    void deleteKnowledgeBase(String kbId);
}
