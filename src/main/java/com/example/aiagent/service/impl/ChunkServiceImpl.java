package com.example.aiagent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aiagent.mapper.ChunkMapper;
import com.example.aiagent.model.entity.Chunk;
import com.example.aiagent.service.ChunkService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChunkServiceImpl extends ServiceImpl<ChunkMapper, Chunk> implements ChunkService {

    @Override
    public List<Chunk> similaritySearch(String kbId, String vectorLiteral, int limit) {
        return baseMapper.similaritySearch(kbId, vectorLiteral, limit);
    }
}
