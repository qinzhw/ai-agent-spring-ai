package com.example.aiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiagent.model.entity.Chunk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChunkMapper extends BaseMapper<Chunk> {

    /**
     * pgvector L2 距离相似度检索
     *
     * @param kbId           知识库ID
     * @param vectorLiteral  向量字符串，如 [0.1,0.2,...]
     * @param limit          返回条数
     * @return 按距离升序排列的分块列表
     */
    List<Chunk> similaritySearch(
            @Param("kbId") String kbId,
            @Param("vectorLiteral") String vectorLiteral,
            @Param("limit") int limit
    );
}
