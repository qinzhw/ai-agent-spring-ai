package com.example.aiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiagent.model.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 按会话 ID 查询最近的 N 条消息（用于恢复记忆）
     */
    List<ChatMessage> selectBySessionIdRecently(
            @Param("sessionId") String sessionId,
            @Param("limit") int limit
    );
}
