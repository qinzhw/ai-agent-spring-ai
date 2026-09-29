package com.example.aiagent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aiagent.model.entity.Chunk;

import java.util.List;

public interface ChunkService extends IService<Chunk> {

    /**
     * pgvector L2 距离相似度检索
     */
    List<Chunk> similaritySearch(String kbId, String vectorLiteral, int limit);
}
